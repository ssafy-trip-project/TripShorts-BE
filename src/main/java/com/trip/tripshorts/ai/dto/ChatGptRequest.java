package com.trip.tripshorts.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ChatGptRequest(
        String model,
        List<ChatMessage> messages,
        double temperature,
        @JsonProperty("max_tokens")
        int maxTokens) {
}
