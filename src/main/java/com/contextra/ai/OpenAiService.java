package com.contextra.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${openai.api-key}")
    private String apiKey;

    private static final String EMBEDDING_URL = "https://api.openai.com/v1/embeddings";
    private static final String CHAT_URL = "https://api.openai.com/v1/chat/completions";

    public List<Double> getEmbedding(String text) {
        try {
            Map<String, Object> request = Map.of(
                    "input", text,
                    "model", "text-embedding-3-small"
            );

            Map response = restTemplate.postForObject(
                    EMBEDDING_URL,
                    new org.springframework.http.HttpEntity<>(request, getHeaders()),
                    Map.class
            );

            List<Map> data = (List<Map>) response.get("data");
            return (List<Double>) data.get(0).get("embedding");

        } catch (Exception e) {
            throw new RuntimeException("Embedding API failed", e);
        }
    }

    public String chat(String prompt) {
        try {
            Map<String, Object> request = Map.of(
                    "model", "gpt-4o-mini",
                    "messages", List.of(Map.of("role", "user", "content", prompt))
            );

            Map response = restTemplate.postForObject(
                    CHAT_URL,
                    new org.springframework.http.HttpEntity<>(request, getHeaders()),
                    Map.class
            );

            List<Map> choices = (List<Map>) response.get("choices");
            Map message = (Map) choices.get(0).get("message");

            return message.get("content").toString();

        } catch (Exception e) {
            return "Error processing request. Please try again.";
        }
    }

    private org.springframework.http.HttpHeaders getHeaders() {
        var headers = new org.springframework.http.HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.set("Content-Type", "application/json");
        return headers;
    }
}