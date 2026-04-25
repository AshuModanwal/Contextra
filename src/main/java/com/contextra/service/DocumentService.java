package com.contextra.service;

import com.contextra.ai.OpenAiService;
import com.contextra.ai.VectorStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final OpenAiService openAiService;
    private final VectorStoreService vectorStore;

    public void upload(UUID sessionId, String content){

        List<String> chunks = chunk(content);

        for (String chunk : chunks) {
            List<Double> embedding = openAiService.getEmbedding(chunk);
            vectorStore.add(sessionId, chunk, embedding);
        }
    }

    // ✅ Better chunking (paragraph-based)
    private List<String> chunk(String text) {

        String[] paragraphs = text.split("\\n\\n");
        List<String> chunks = new ArrayList<>();

        for (String para : paragraphs) {
            if (para.length() > 500) {
                for (int i = 0; i < para.length(); i += 500) {
                    chunks.add(para.substring(i, Math.min(i + 500, para.length())));
                }
            } else {
                chunks.add(para);
            }
        }

        return chunks;
    }
}