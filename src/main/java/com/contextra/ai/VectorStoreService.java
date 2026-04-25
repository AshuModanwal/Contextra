package com.contextra.ai;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class VectorStoreService {

    static class VectorEntry {
        UUID sessionId;
        String content;
        List<Double> embedding;

        VectorEntry(UUID sessionId, String content, List<Double> embedding) {
            this.sessionId = sessionId;
            this.content = content;
            this.embedding = embedding;
        }
    }

    // ✅ Thread-safe list
    private final List<VectorEntry> store = new CopyOnWriteArrayList<>();

    public void add(UUID sessionId, String content, List<Double> embedding) {
        store.add(new VectorEntry(sessionId, content, embedding));
    }

    public List<String> search(UUID sessionId, List<Double> queryEmbedding, int topK) {

        return store.stream()
                .filter(e -> e.sessionId.equals(sessionId))
                .sorted(Comparator.comparingDouble(e ->
                        -cosineSimilarity(e.embedding, queryEmbedding)))
                .limit(topK)
                .map(e -> e.content)
                .toList();
    }

    private double cosineSimilarity(List<Double> a, List<Double> b) {
        double dot = 0, normA = 0, normB = 0;

        for (int i = 0; i < a.size(); i++) {
            dot += a.get(i) * b.get(i);
            normA += Math.pow(a.get(i), 2);
            normB += Math.pow(b.get(i), 2);
        }

        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}