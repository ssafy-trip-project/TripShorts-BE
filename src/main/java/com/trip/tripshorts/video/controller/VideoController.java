package com.trip.tripshorts.video.controller;

import com.trip.tripshorts.auth.service.AuthService;
import com.trip.tripshorts.member.domain.Member;
import com.trip.tripshorts.video.domain.Video;
import com.trip.tripshorts.video.dto.*;
import com.trip.tripshorts.video.repository.VideoRepository;
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

    @PostMapping("/{videoId}/view")
    public ResponseEntity<?> increaseView(@PathVariable Long videoId) {
        videoService.increaseView(videoId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<VideoInfoResponse> getVideoInfo(@RequestParam("videoId") Long videoId) {
        return ResponseEntity.ok(videoService.getVideoInfo(videoId));
    }

    @GetMapping("/feed")
    public ResponseEntity<VideoPageResponse> getVideoPages(
            @RequestParam("sortby") String sortBy,
            @RequestParam("cursorid") Long cursorId,
            @RequestParam(required = false, defaultValue = "5") int size
    ){
        log.info("pagination controller in");
        return ResponseEntity.ok(videoService.getVideoPage(sortBy, cursorId, size));
    }

    @GetMapping("/my-videos")
    public ResponseEntity<List<VideoListResponse>> getMyVideos(
            @RequestParam(value = "id", required = false) Long id
    ) {
        List<VideoListResponse> myVideos = videoService.getMyVideos(id);
        log.debug("videos Size: {}", myVideos.size());
        return ResponseEntity.ok(myVideos);
    }


    @GetMapping("/my-videos/feed")
    public ResponseEntity<VideoFeedResponse> getMyVideoFeed() {
        return ResponseEntity.ok(videoService.getMyVideoFeed());
    }

    @DeleteMapping("/my-videos/{videoId}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long videoId) {
        videoService.deleteVideo(videoId);
        return ResponseEntity.ok().build();
    }
}