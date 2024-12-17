package com.trip.tripshorts.video.service;

import com.trip.tripshorts.ai.dto.GeneratedTagsResponse;
import com.trip.tripshorts.ai.service.OpenAiService;
import com.trip.tripshorts.auth.service.AuthService;
import com.trip.tripshorts.member.domain.Member;
import com.trip.tripshorts.tag.dto.TagListResponse;
import com.trip.tripshorts.tag.dto.TagResponseDto;
import com.trip.tripshorts.tag.repository.TagRepository;
import com.trip.tripshorts.tour.domain.Tour;
import com.trip.tripshorts.tour.repository.TourRepository;
import com.trip.tripshorts.util.S3Service;
import com.trip.tripshorts.video.domain.Video;
import com.trip.tripshorts.video.dto.*;
import com.trip.tripshorts.video.repository.VideoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoService {

    private final VideoRepository videoRepository;
    private final S3Service s3Service;
    private final AuthService authService;
    private final TourRepository tourRepository;
    private final OpenAiService openAiService;
    private final TagRepository tagRepository;
    private static final int DEFAULT_FEED_SIZE = 5;

    @Transactional
    public VideoCreateResponse createVideo(VideoCreateRequest videoCreateRequest) {
        Member member = authService.getCurrentMember();
        Tour tour = tourRepository.findById(videoCreateRequest.getTourId())
                .orElseThrow(()-> new EntityNotFoundException("해당 관광지를 찾을 수 없습니다."));

        GeneratedTagsResponse tagsResponse = openAiService.generateTags(tour.getTitle());

        Video video = Video.builder()
                .videoUrl(videoCreateRequest.getVideoUrl())
                .thumbnailUrl(videoCreateRequest.getThumbnailUrl())
                .member(member)
                .tour(tour)
                .tags(new ArrayList<>())
                .build();

        video.addTags(tagsResponse.tags());

        return VideoCreateResponse.from(videoRepository.save(video));
    }

    @Transactional(readOnly = true)
    public List<VideoListResponse> getVideos(String sortBy) {
        return switch (sortBy) {
            case "recent" -> videoRepository.findAllOrderByCreatedDateDesc();
            case "likes" -> videoRepository.findAllOrderByLikesDesc();
            case "views" -> videoRepository.findAllOrderByViewCountDesc();
            default -> videoRepository.findAllOrderByCreatedDateDesc();
        };
    }


    public VideoInfoResponse getVideoInfo(Long videoId) {
        Tour tour = videoRepository.findTourByVideoId(videoId)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found for video id: " + videoId));

        return new VideoInfoResponse(videoId, tour);
    }

    @Transactional(readOnly = true)
    public VideoPageResponse getVideoPage(String sortBy, Long currentVideoId, int size) {
        log.debug("Fetching videos with cursorId: {}, size: {}", currentVideoId, size);
        Member currentMember = authService.getCurrentMember();
        final int streamingVideoSize = size/2;

        Video currentVideo = videoRepository.findById(currentVideoId)
                .orElseThrow(EntityNotFoundException::new);

        List<Video> prevVideos = switch(sortBy){
            case "recent" -> videoRepository.findPreviousByRecent(currentVideo.getCreatedDate(), currentVideoId, streamingVideoSize);
            case "likes" -> videoRepository.findPreviousByLikes(currentVideoId, streamingVideoSize);
            default -> videoRepository.findPreviousByRecent(currentVideo.getCreatedDate(), currentVideoId, streamingVideoSize);
        };

        List<Video> nextVideos = switch(sortBy){
            case "recent" -> videoRepository.findNextByRecent(currentVideo.getCreatedDate(), currentVideoId, streamingVideoSize);
            case "likes" -> videoRepository.findNextByLikes(currentVideoId, streamingVideoSize);
            default -> videoRepository.findNextByRecent(currentVideo.getCreatedDate(), currentVideoId, streamingVideoSize);
        };

        VideoResponse currentVideoResponse = VideoResponse.from(currentVideo, currentMember);

        List<Long> previousVideoIds = prevVideos.stream()
                .map(Video::getId)
                .toList();

        List<Long> nextVideoIds = nextVideos.stream()
                .map(Video::getId)
                .toList();

        return VideoPageResponse.of(currentVideoResponse, previousVideoIds, nextVideoIds);
    }

    private String extractKeyFromUrl(String url) {
        try {
            return url.split(".com/")[1];
        } catch (Exception e) {
            log.error("Failed to extract key from URL: {}", url, e);
            throw new IllegalArgumentException("Failed to extract key from URL", e);
        }
    }

    @Transactional
    public void increaseView(Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new EntityNotFoundException("Video not found: " + videoId));

        video.raiseView();
    }

    public List<VideoListResponse> getMyVideos(Long id) {
        return videoRepository.findVideosById(id == null ? authService.getCurrentMember().getId() : id);
    }

    @Transactional(readOnly = true)
    public VideoFeedResponse getMyVideoFeed(Long targetId) {
        Member currentMember = authService.getCurrentMember();
        List<Video> videos;
        log.debug("targetId = {}", targetId);
        if(targetId != null && !currentMember.getId().equals(targetId)){
            videos = videoRepository.findAllByMemberIdWithDetails(targetId);
        } else{
            videos = videoRepository.findAllByMemberIdWithDetails(currentMember.getId());
        }

        log.debug("videos.size() = {}", videos.size());
        List<VideoResponse> videoResponses = videos.stream()
                .map(video-> VideoResponse.from(video, currentMember))
                .toList();
        return VideoFeedResponse.of(videoResponses);
    }

    public void deleteVideo(Long videoId) {
        Member currentMember = authService.getCurrentMember();
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Failed to find video with id: " + videoId));

        if(!currentMember.equals(video.getMember())) {
            throw new IllegalArgumentException("Illegal User for this video: " + videoId);
        }

        currentMember.getVideos().remove(video);
        Tour tour = video.getTour();
        tour.getVideos().remove(video);

        videoRepository.delete(video);
        return;
    }
}