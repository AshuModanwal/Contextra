package com.contextra.service;

import com.contextra.entity.ChatMessage;

import java.util.List;
import java.util.UUID;

public interface ChatService {

    UUID createSession();

    void saveMessage(UUID sessionId, ChatMessage.Role role, String content);

    List<ChatMessage> getChatHistory(UUID sessionId);

    List<ChatMessage> getRecentMessages(UUID sessionId, int limit);
}