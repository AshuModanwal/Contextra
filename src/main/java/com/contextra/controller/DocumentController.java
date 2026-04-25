package com.contextra.controller;

import com.contextra.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public String upload(@RequestParam UUID sessionId,
                         @RequestBody String content) {

        documentService.upload(sessionId, content);
        return "Document uploaded and indexed successfully";
    }
}