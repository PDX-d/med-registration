package org.example.service.impl;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.common.result.Result;
import org.example.pojo.entity.ChatMessage;
import org.example.common.properties.AIProperties;
import org.example.service.AIService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.json.JSONObject;
import org.example.pojo.entity.History;
import org.example.common.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.example.common.constants.RedisConstant.AI_HISTORY_KEY;

@Service
@Slf4j
public class AIServiceImpl implements AIService {

	@Resource
	private AIProperties aiProperties;

	@Resource
	private StringRedisTemplate stringRedisTemplate;

	private static final String ROLE = "role";
	private static final String TYPE = "type";
	private static final String CONTENT_TYPE = "content_type";
	private static final String CONTENT = "content";

	@Override
	public void ai(ChatMessage chatMessage, HttpServletResponse response) {
		//告诉浏览器这是流式输出text/event-stream
		response.setContentType("text/event-stream;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Connection", "keep-alive");
		//告诉nginx 浏览器不要缓存
		response.setHeader("X-Accel-Buffering", "no");
		HttpURLConnection conn = null;

		Long userId = UserHolder.getUser().getId();
		saveHistory(userId, "user", chatMessage.getMessage(),null,null);

		try {
			List<Object> msgList = new ArrayList<>();
			List<History> history = chatMessage.getHistory();
			// 历史对话
			if (history != null && !history.isEmpty()) {
				for (History msg : history) {
					msgList.add(JSONUtil.createObj()
							.set(ROLE, msg.getRole())
							.set(TYPE, "question")
							.set(CONTENT_TYPE, "text")
							.set(CONTENT, msg.getContent()));
				}
			}
			// 当前问题
			msgList.add(JSONUtil.createObj()
					.set(ROLE, "user")
					.set(TYPE, "question")
					.set(CONTENT_TYPE, "text")
					.set(CONTENT, chatMessage.getMessage()));

			//打包请求体
			String body = JSONUtil.createObj()
					.set("bot_id", aiProperties.getBOT_ID())
					.set("user_id", chatMessage.getUserId())
					.set("stream", true)
					.set("additional_messages", msgList)
					.toString();

			//发送给coze大模型
			URL url = new URL(aiProperties.getCOZE_URL());
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "Bearer " + aiProperties.getTOKEN());
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(60000);

			try (OutputStream os = conn.getOutputStream()) {
				os.write(body.getBytes(StandardCharsets.UTF_8));
				os.flush();
			}

			// 用来拼接 AI 完整回答Redis
			StringBuilder aiFullReasonContent = new StringBuilder();
			StringBuilder aiFullContent = new StringBuilder();
			//读取流式响应
			try (InputStream is = conn.getInputStream();
				 BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
				String line;
				String currentEvent = "";
				while ((line = reader.readLine()) != null) {
					// 检查客户端是否断开连接
					if (response.getWriter().checkError()) {
						log.info("客户端已断开连接，停止请求");
						break;
					}
					log.info("Coze返回: {}", line);
					if (line.startsWith("event:")) {
						currentEvent = line.substring(6).trim();
						continue;
					}
					if (!line.startsWith("data:")) {
						continue;
					}
					String data = line.substring(5).trim();
					if (data.isEmpty()) {
						continue;
					}
					// 过滤掉 Coze 内部结束包，不再返回给前端
					if (data.contains("generate_answer_finish")) {
						continue;
					}
					if (currentEvent.contains("conversation.message.delta") ||
							currentEvent.contains("conversation.message.completed")) {
						try {
							JSONObject jsonObj = JSONUtil.parseObj(data);

							String reasoningContent = jsonObj.getStr("reasoning_content", "");
							if (reasoningContent != null && !reasoningContent.isEmpty()) {
								aiFullReasonContent.append(reasoningContent);
								reasoningContent = reasoningContent.replace("\\", "\\\\")
										.replace("\"", "\\\"")
										.replace("\n", "\\n")
										.replace("\r", "\\r")
										.replace("\t", "\\t");
								//推送给前端浏览器
								response.getWriter().write("data: {\"reasoning\":\"" + reasoningContent + "\"}\n\n");
								//立刻发送
								response.getWriter().flush();
							}
							String content = jsonObj.getStr("content", "");
							if (content != null && !content.isEmpty()) {
								aiFullContent.append(content);
								content = content.replace("\\", "\\\\")
										.replace("\"", "\\\"")
										.replace("\n", "\\n")
										.replace("\r", "\\r")
										.replace("\t", "\\t");

								response.getWriter().write("data: {\"content\":\"" + content + "\"}\n\n");
								response.getWriter().flush();
							}
						} catch (Exception e) {
							log.error("解析失败", e);
						}
					}
				}
				// 最后正常结束
				saveHistory(userId, "assistant",
						aiFullContent.toString(),
						aiFullReasonContent.toString(),
						null); // questions 由前端返回时自带
				response.getWriter().write("data: [DONE]\n\n");
				response.getWriter().flush();
			}
		} catch (Exception e) {
			log.error("Coze调用异常", e);
		} finally {
			// 确保关闭到Coze的连接
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	private void saveHistory(Long userId, String role, String content, String reasoning, List<String> questions) {
		//保存历史记录
		String key = AI_HISTORY_KEY + userId;
		History history = new History();
		history.setRole(role);
		history.setContent(content);
		history.setReasoning(reasoning);
		history.setQuestions(questions);
		history.setShowReasoning(false);
		history.setTime(getCurrentTime());
		// 转 JSON
		String json = JSONUtil.toJsonStr(history);
		stringRedisTemplate.opsForList().rightPush(key, json);
		// 设置过期时间 7 天
		stringRedisTemplate.expire(key, 7, java.util.concurrent.TimeUnit.DAYS);
 	}
	// 获取当前时间 16:20
	private String getCurrentTime() {
		return new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date());
	}
	@Override
	public Result history(Long userId) {
		//获取历史记录
		String key = AI_HISTORY_KEY + userId;
		List<String> jsonList = stringRedisTemplate.opsForList().range(key, 0, 20);
		List<History> list = new ArrayList<>();
		if (jsonList != null) {
			for (String json : jsonList) {
				History h = JSONUtil.toBean(json, History.class);
				list.add(h);
			}
		}
		return Result.success(list);
	}
}
