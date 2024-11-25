package com.trip.tripshorts.ai.dto;

import java.util.List;

public record ChatGptRequest(String model, List<ChatMessage> messages, double temperature, int maxTokens) {
}
