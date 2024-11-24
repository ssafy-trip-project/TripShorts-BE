package com.trip.tripshorts.video.controller;

import com.trip.tripshorts.auth.service.AuthService;
import com.trip.tripshorts.member.domain.Member;
import com.trip.tripshorts.video.dto.*;
import com.trip.tripshorts.video.service.VideoService;
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
public class VideoController {

    private final VideoService videoService;
    private final AuthService authService;

    @GetMapping("/presigned-url")
    public ResponseEntity<Map<String, String>> getPresignedUrlForUpload(
            @RequestParam String filename,
            @RequestParam String contentType) {
        String presignedUrl = videoService.getPresignedUrlForUpload(filename, contentType);
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

    @GetMapping
    public ResponseEntity<List<VideoListResponse>> getVideos(@RequestParam("sortby") String sortBy) {
        return ResponseEntity.ok(videoService.getVideos(sortBy));
    }

    @GetMapping("/search")
    public ResponseEntity<VideoInfoResponse> getVideoInfo(@RequestParam("videoId") Long videoId) {
        return ResponseEntity.ok(videoService.getVideoInfo(videoId));
    }

    @GetMapping("/feed")
    public ResponseEntity<VideoPageResponse> getVideoPages(
            @RequestParam(required = false) Long cursorId,
            @RequestParam int size
    ){
        log.info("pagination controller in");
        return ResponseEntity.ok(videoService.getVideoPage(cursorId, size));
    }

}