package com.trip.tripshorts.video.controller;

import com.trip.tripshorts.video.domain.Shorts;
import com.trip.tripshorts.video.dto.ShortsCreateRequest;
import com.trip.tripshorts.video.service.ShortsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/shorts")
@RequiredArgsConstructor
@Slf4j
public class ShortsController {

    private final ShortsService shortsService;

    @GetMapping("/presigned-url")
    public ResponseEntity<Map<String, String>> getPresignedUrl(
            @RequestParam String filename,
            @RequestParam String contentType) {
        String presignedUrl = shortsService.getPresignedUrl(filename, contentType);
        log.info(presignedUrl);
        Map<String, String> response = new HashMap<>();
        response.put("presignedUrl", presignedUrl);
        response.put("filename", filename);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Shorts> createShorts(@RequestBody ShortsCreateRequest request) {
        // User 정보는 나중에 Security 구현 후 추가
        Shorts shorts = shortsService.createShorts(
                request.getVideoUrl(),
                request.getOriginalFileName(),
                request.getDescription()
        );
        return ResponseEntity.ok(shorts);
    }

}