package com.trip.tripshorts.video.service;

import com.trip.tripshorts.auth.service.AuthService;
import com.trip.tripshorts.member.domain.Member;
import com.trip.tripshorts.member.repository.MemberRepository;
import com.trip.tripshorts.tour.domain.Tour;
import com.trip.tripshorts.video.domain.Video;
import com.trip.tripshorts.video.dto.VideoCreateRequest;
import com.trip.tripshorts.video.dto.VideoCreateResponse;
import com.trip.tripshorts.video.dto.VideoInfoResponse;
import com.trip.tripshorts.video.dto.VideoListResponse;
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

    public String getPresignedUrl(String filename, String contentType) {
        return s3Service.generatePresignedUrl(filename, contentType);
    }

    @Transactional
    public VideoCreateResponse createVideo(VideoCreateRequest videoCreateRequest) {
        Member member = authService.getCurrentMember();

        Video video = Video.builder()
                .videoUrl(videoCreateRequest.getVideoUrl())
                .thumbnailUrl(videoCreateRequest.getThumbnailUrl())
                .member(member)
                .build();

        return VideoCreateResponse.from(videoRepository.save(video));
    }

    @Transactional(readOnly = true)
    public List<VideoListResponse> getVideos() {
        return videoRepository.fetchVideoList();
    }


    public VideoInfoResponse getVideoInfo(Long videoId) {
        Tour tour = videoRepository.findTourByVideoId(videoId)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found for video id: " + videoId));

        return new VideoInfoResponse(videoId, tour);
    }
}