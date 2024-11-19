package com.trip.tripshorts.video.controller;

import com.trip.tripshorts.video.dto.VideoCreateRequest;
import com.trip.tripshorts.video.dto.VideoCreateResponse;
import com.trip.tripshorts.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/shorts")
@RequiredArgsConstructor
@Slf4j
public class VideoController {

    private final VideoService videoService;

    @GetMapping("/presigned-url")
    public ResponseEntity<Map<String, String>> getPresignedUrl(
            @RequestParam String filename,
            @RequestParam String contentType) {
        String presignedUrl = videoService.getPresignedUrl(filename, contentType);
        log.info(presignedUrl);
        Map<String, String> response = new HashMap<>();
        response.put("presignedUrl", presignedUrl);
        response.put("filename", filename);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<VideoCreateResponse> createVideo(@RequestBody VideoCreateRequest videoCreateRequest) {
        return ResponseEntity.ok(videoService.createVideo(videoCreateRequest));
    }

}