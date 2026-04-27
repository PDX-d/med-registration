package org.example.service;

import org.example.common.result.Result;
import org.example.pojo.entity.ChatMessage;

import javax.servlet.http.HttpServletResponse;

public interface AIService {
	void ai(ChatMessage chatMessage, HttpServletResponse response);

	Result history(Long userId);
}
