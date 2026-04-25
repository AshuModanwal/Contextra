package com.contextra.service.impl;

import com.contextra.entity.ChatMessage;
import com.contextra.entity.ChatSession;
import com.contextra.repository.ChatMessageRepository;
import com.contextra.repository.ChatSessionRepository;
import com.contextra.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;

    @Override
    public UUID createSession() {
        ChatSession session = ChatSession.builder().build();
        return sessionRepository.save(session).getId();
    }

    @Override
    public void saveMessage(UUID sessionId, ChatMessage.Role role, String content) {
        ChatMessage message = ChatMessage.builder()
                .sessionId(sessionId)
                .role(role)
                .content(content)
                .build();

        messageRepository.save(message);
    }

    @Override
    public List<ChatMessage> getChatHistory(UUID sessionId) {
        return messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }

    @Override
    public List<ChatMessage> getRecentMessages(UUID sessionId, int limit) {
        List<ChatMessage> messages =
                messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);

        int size = messages.size();

        if (size <= limit) return messages;

        return messages.subList(size - limit, size);
    }
}