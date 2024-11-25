package com.trip.tripshorts.ai.service;

import com.trip.tripshorts.ai.dto.ChatGptRequest;
import com.trip.tripshorts.ai.dto.ChatGptResponse;
import com.trip.tripshorts.ai.dto.ChatMessage;
import com.trip.tripshorts.ai.dto.GeneratedTagsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

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

    public GeneratedTagsResponse generateTags(String location) {
        ChatGptRequest request = createChatRequest(location);
        ChatGptResponse response = callGptApi(request);

        return parseTagsFromResponse(response);
    }

    private ChatGptRequest createChatRequest(String location) {
        String prompt = String.format(
                "다음 여행 장소와 관련된 인스타그램용 해시태그를 5개 생성해주세요. " +
                        "각 태그는 '#' 없이 쉼표로 구분해서 반환해주세요. " +
                        "장소: %s",
                location
        );

        return new ChatGptRequest(
                model,
                List.of(new ChatMessage("user", prompt)),
                0.7,
                150
        );
    }

    private ChatGptResponse callGptApi(ChatGptRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<ChatGptRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<ChatGptResponse> response = restTemplate.exchange(
                    "https://api.openai.com/v1/chat/completions",
                    HttpMethod.POST,
                    entity,
                    ChatGptResponse.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("GPT API 호출 실패", e);
            throw new RuntimeException("태그 생성 실패", e);
        }
    }

    private GeneratedTagsResponse parseTagsFromResponse(ChatGptResponse response) {
        String content = response.choices().get(0).message().content();
        List<String> tags = Arrays.stream(content.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .toList();
        return new GeneratedTagsResponse(tags);
    }

}
