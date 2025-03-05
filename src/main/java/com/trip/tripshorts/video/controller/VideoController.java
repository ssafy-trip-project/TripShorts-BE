package com.trip.tripshorts.video.controller;

import com.trip.tripshorts.util.S3Service;
import com.trip.tripshorts.video.dto.*;
import com.trip.tripshorts.video.service.HlsConverter;
import com.trip.tripshorts.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/shorts")
@RequiredArgsConstructor
@Slf4j
public class VideoController {

    private final VideoService videoService;
    private final S3Service s3Service;
    private final HlsConverter hlsConverter;

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

//    @PostMapping
//    public ResponseEntity<VideoCreateResponse> createVideo(@RequestBody VideoCreateRequest videoCreateRequest) {
//        return ResponseEntity.ok(videoService.createVideo(videoCreateRequest));
//    }

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

    @PostMapping("/upload")
    public ResponseEntity<String> uploadShort(
            @RequestPart("video") MultipartFile videoFile,
            @RequestPart("thumbnail") MultipartFile thumbnailFile,
            @RequestParam("tourId") Long tourId) { // ✅ @RequestPart → @RequestParam 변경

        log.info("Received Video: " + videoFile.getOriginalFilename());
        log.info("Received Thumbnail: " + thumbnailFile.getOriginalFilename());
        log.info("Received tourId: " + tourId);

        String outputDir = "C:\\Users\\seowj\\Desktop\\hls_output";
        try {
            // HLS 변환 및 S3 업로드 후 .m3u8 URL 반환
            String hlsUrl = hlsConverter.convertToHls(videoFile, outputDir);

            // 썸네일을 S3에 업로드 후 URL 반환
            String thumbnailUrl = s3Service.uploadFile(thumbnailFile);

            videoService.createVideo(hlsUrl, thumbnailUrl, tourId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok("Upload successful");
    }
}