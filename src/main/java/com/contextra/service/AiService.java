package com.contextra.service;

import java.util.UUID;

public interface AiService {

    String askQuestion(UUID sessionId, String question);

}