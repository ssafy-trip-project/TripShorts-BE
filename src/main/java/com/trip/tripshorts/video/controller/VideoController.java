package com.trip.tripshorts.video.controller;

import com.trip.tripshorts.util.S3Service;
import com.trip.tripshorts.video.dto.*;
import com.trip.tripshorts.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/shorts")
@RequiredArgsConstructor
@Slf4j
public class VideoController {

    private final VideoService videoService;
    private final S3Service s3Service;

    @GetMapping("/presigned-url")
    public ResponseEntity<List<PresignedUrlResponse>> getPresignedUrlsForUpload(
            @RequestParam List<String> filenames,
            @RequestParam List<String> contentTypes) {

        if (filenames.size() != contentTypes.size()) {
            return ResponseEntity.badRequest().build();
        }

        List<PresignedUrlResponse> urls = new ArrayList<>();
        for (int i = 0; i < filenames.size(); i++) {
            String presignedUrl = s3Service.generatePresignedUrlForUpload(filenames.get(i), contentTypes.get(i));
            urls.add(new PresignedUrlResponse(presignedUrl, filenames.get(i)));
        }

        return ResponseEntity.ok(urls);
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

    @GetMapping("/feed/{direction}")
    public ResponseEntity<List<VideoResponse>> getNextVideoPages(
            @PathVariable("direction") String direction,
            @RequestParam("sortby") String sortBy,
            @RequestParam("cursorid") Long cursorId,
            @RequestParam(required = false, defaultValue = "2") int size
    ){

        return ResponseEntity.ok(videoService.getMoreVideopage(direction, sortBy, cursorId, size));
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
    public ResponseEntity<VideoFeedResponse> getMyVideoFeed(
            @RequestParam(required = false) Long targetId
    ) {
        return ResponseEntity.ok(videoService.getMyVideoFeed(targetId));
    }

    @DeleteMapping("/my-videos/{videoId}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long videoId) {
        videoService.deleteVideo(videoId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/s3test")
    public ResponseEntity<Void> s3test() {
        try {
            s3Service.downloadFile("/videos/shorts/10.mp4", "C:\\Users");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok().build();
    }
}