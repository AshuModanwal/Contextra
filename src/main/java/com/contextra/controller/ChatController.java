package com.contextra.controller;

import com.contextra.service.AiService;
import com.contextra.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final AiService aiService;
    private final ChatService chatService;

    @PostMapping("/session")
    public UUID createSession() {
        return chatService.createSession();
    }

    @PostMapping
    public String chat(@RequestParam UUID sessionId,
                       @RequestParam String question) {

        return aiService.askQuestion(sessionId, question);
    }
}