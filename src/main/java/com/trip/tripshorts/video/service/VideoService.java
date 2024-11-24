package com.trip.tripshorts.video.service;

import com.trip.tripshorts.auth.service.AuthService;
import com.trip.tripshorts.member.domain.Member;
import com.trip.tripshorts.member.repository.MemberRepository;
import com.trip.tripshorts.tour.domain.Tour;
import com.trip.tripshorts.tour.repository.TourRepository;
import com.trip.tripshorts.video.domain.Video;
import com.trip.tripshorts.video.dto.*;
import com.trip.tripshorts.video.repository.VideoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoService {

    private final VideoRepository videoRepository;
    private final S3Service s3Service;
    private final MemberRepository memberRepository;
    private final AuthService authService;
    private final TourRepository tourRepository;

    public String getPresignedUrlForUpload(String filename, String contentType) {
        return s3Service.generatePresignedUrlForUpload(filename, contentType);
    }

    @Transactional
    public VideoCreateResponse createVideo(VideoCreateRequest videoCreateRequest) {
        Member member = authService.getCurrentMember();
        Tour tour = tourRepository.findById(videoCreateRequest.getTourId())
                .orElseThrow(()-> new EntityNotFoundException("해당 관광지를 찾을 수 없습니다."));

        Video video = Video.builder()
                .videoUrl(videoCreateRequest.getVideoUrl())
                .thumbnailUrl(videoCreateRequest.getThumbnailUrl())
                .member(member)
                .tour(tour)
                .build();

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
    public VideoPageResponse getVideoPage(Long cursorId, int size) {
        log.debug("Fetching videos with cursorId: {}, size: {}", cursorId, size);
        Member currentMember = authService.getCurrentMember();

        List<Video> videos = videoRepository.findVideosByCursorId(cursorId, size+1);

        log.debug("Found videos with ids: {}",
                videos.stream()
                        .map(Video::getId)
                        .toList());

        boolean hasNext = videos.size() > size;
        if(hasNext){
            videos = videos.subList(0, videos.size()-1);
        }

        List<VideoResponse> videoResponses = videos.stream()
                .map(video -> {
                    log.debug(video.getVideoUrl());
                    String videoKey = extractKeyFromUrl(video.getVideoUrl());
                    String thumbnailKey = extractKeyFromUrl(video.getThumbnailUrl());
                    log.debug("videokey = {}", videoKey);
                    return VideoResponse.builder()
                            .id(video.getId())
                            .videoUrl(s3Service.generatePresignedUrlForDownload(videoKey))
                            .thumbnailUrl(s3Service.generatePresignedUrlForDownload(thumbnailKey))
                            .creator(VideoCreatorDto.from(video.getMember()))
                            .likeCount(video.getLikes().size())
                            .commentCount(video.getComments().size())
                            .createdAt(video.getCreatedDate())
                            .liked(video.getLikes().stream()
                                    .anyMatch(like -> like.getMember().getId().equals(currentMember.getId())))
                            .build();
                })
                .toList();

        Long lastVideoId = videos.isEmpty() ? null : videos.get(videos.size()-1).getId();

        log.debug("Returning response with nextCursor: {}, hasNext: {}",
                lastVideoId, hasNext);

        return VideoPageResponse.of(videoResponses, lastVideoId, hasNext);
    }

    private String extractKeyFromUrl(String url) {
        try {
            return url.split("cloudfront.net/")[1];
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

    public MyVideoPageResponse getMyVideos(Long cursorId, int size) {
        Member currentMember = authService.getCurrentMember();

        // size + 1로 다음 페이지 존재 여부 확인
        List<Video> videos = videoRepository.findMyVideosByCursor(
                currentMember.getId(),
                cursorId,
                size + 1
        );

        boolean hasNext = videos.size() > size;
        List<Video> pagedVideos = hasNext ? videos.subList(0, size) : videos;

        List<MyVideoListResponse> videoResponses = pagedVideos.stream()
                .map(video -> MyVideoListResponse.builder()
                        .videoId(video.getId())
                        .thumbnailUrl(video.getThumbnailUrl())
                        .tourName(video.getTour().getTitle())
                        .build())
                .toList();

        Long lastVideoId = videoResponses.isEmpty() ? null :
                videoResponses.get(videoResponses.size() - 1).getVideoId();

        return MyVideoPageResponse.of(
                currentMember.getNickname(),
                videoResponses,
                lastVideoId,
                hasNext
        );
    }

    public VideoPageResponse getMyVideoPages(Long cursorId, int size) {
        Member currentMember = authService.getCurrentMember();
        List<Video> videos = videoRepository.findMyVideosByCursor(
                currentMember.getId(),
                cursorId,
                size + 1
        );
        boolean hasNext = videos.size() > size;
        List<Video> pagedVideos = hasNext ? videos.subList(0, size) : videos;

        List<VideoResponse> videoResponses = pagedVideos.stream()
                .map(video -> VideoResponse.builder()
                        .id(video.getId())
                        .videoUrl(video.getVideoUrl())
                        .thumbnailUrl(video.getThumbnailUrl())
                        .creator(VideoCreatorDto.from(video.getMember()))
                        .likeCount(video.getLikes().size())
                        .commentCount(video.getComments().size())
                        .createdAt(video.getCreatedDate())
                        .liked(video.getLikes().stream()
                                .anyMatch(like -> like.getMember().getId().equals(currentMember.getId())))
                        .build())
                .toList();

        Long lastVideoId = videoResponses.isEmpty() ? null :
                videoResponses.get(videoResponses.size()-1).getId();

        return VideoPageResponse.of(videoResponses, lastVideoId, hasNext);
    }
}