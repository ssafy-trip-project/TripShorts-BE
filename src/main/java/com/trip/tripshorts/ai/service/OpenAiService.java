package com.trip.tripshorts.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiService {

    private static final String CHAT_URL = "https://api.openai.com/v1/chat/completions";
    private final RestTemplate restTemplate;

    @Value("${openai.api-key}")
    private String apiKey;
    @Value("${openai.model}")
    private String model;

}
