package com.trip.tripshorts.ai.dto;

import java.util.List;

public record ChatGptResponse(List<ChatChoice> choices) {
}
