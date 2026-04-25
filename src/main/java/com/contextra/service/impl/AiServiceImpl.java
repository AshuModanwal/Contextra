package com.contextra.service.impl;

import com.contextra.entity.ChatMessage;
import com.contextra.service.AiService;
import com.contextra.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final ChatService chatService;
    private final ChatClient chatClient;

    @Override
    public String askQuestion(UUID sessionId, String question) {

        // 1. Save user message
        chatService.saveMessage(sessionId, ChatMessage.Role.USER, question);

        // 2. Get recent chat history
        List<ChatMessage> history =
                chatService.getRecentMessages(sessionId, 5);

        // 3. Convert history → text
        String conversation = history.stream()
                .map(msg -> msg.getRole() + ": " + msg.getContent())
                .collect(Collectors.joining("\n"));

        // 4. Build prompt
        String prompt = buildPrompt(conversation, question);

        // 5. Call LLM
        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        // 6. Save AI response
        chatService.saveMessage(sessionId, ChatMessage.Role.AI, response);

        return response;
    }

    private String buildPrompt(String conversation, String question) {
        return """
                You are an AI assistant.

                Conversation:
                %s

                Question:
                %s

                Answer clearly and concisely.
                """.formatted(conversation, question);
    }
}