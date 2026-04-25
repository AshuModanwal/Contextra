package com.contextra.ai;

import com.contextra.entity.ChatMessage;
import com.contextra.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationalRagService {

    private final ChatService chatService;
    private final OpenAiService openAiService;
    private final VectorStoreService vectorStore;

    public String ask(UUID sessionId, String question) {

        // 1. Save user message
        chatService.saveMessage(sessionId, ChatMessage.Role.USER, question);

        // 2. Get recent memory
        List<ChatMessage> history = chatService.getRecentMessages(sessionId, 5);

        String conversation = history.stream()
                .map(m -> m.getRole() + ": " + m.getContent())
                .collect(Collectors.joining("\n"));

        // 3. Embed question
        List<Double> queryEmbedding = openAiService.getEmbedding(question);

        // 4. Retrieve relevant context
        List<String> contextList = vectorStore.search(sessionId, queryEmbedding, 3);
        String context = String.join("\n", contextList);

        // 5. Build prompt
        String prompt = """
                You are an AI assistant.

                Context:
                %s

                Conversation:
                %s

                Question:
                %s

                Rules:
                - Answer ONLY from context
                - Use conversation if needed
                - If not found, say "I don't know"
                """.formatted(context, conversation, question);

        // 6. Call LLM
        String response = openAiService.chat(prompt);

        // 7. Save AI response
        chatService.saveMessage(sessionId, ChatMessage.Role.AI, response);

        return response;
    }
}