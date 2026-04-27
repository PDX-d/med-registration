package org.example.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.common.result.Result;
import org.example.pojo.entity.ChatMessage;
import org.example.service.AIService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;


@Api(tags = "AI")
@Slf4j
@RestController
@RequestMapping("/ai/chat")
public class AI {

	@Resource
	private AIService aiService;

	@PostMapping(value = "/stream", produces = "text/event-stream;charset=utf-8")
	@ApiOperation(value = "AI流式对话", notes = "与AI助手进行流式对话交互")
	public void stream(@RequestBody ChatMessage chatMessage, HttpServletResponse response) {
		aiService.ai(chatMessage, response);
	}
	@GetMapping("/history")
	@ApiOperation(value = "获取对话历史", notes = "获取用户的AI对话历史记录")
	public Result history(@RequestParam Long userId) {
		return aiService.history(userId);
	}
}