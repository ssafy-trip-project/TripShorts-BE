package com.trip.tripshorts.video.service;

import com.trip.tripshorts.auth.domain.UserPrincipal;
import com.trip.tripshorts.auth.service.AuthService;
import com.trip.tripshorts.member.domain.Member;
import com.trip.tripshorts.member.repository.MemberRepository;
import com.trip.tripshorts.video.domain.Video;
import com.trip.tripshorts.video.dto.VideoCreateRequest;
import com.trip.tripshorts.video.dto.VideoCreateResponse;
import com.trip.tripshorts.video.repository.VideoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;

        if (principal instanceof UserPrincipal) {
            email = ((UserPrincipal) principal).getEmail();
        } else if (principal instanceof String) {
            email = (String) principal;
        } else {
            throw new RuntimeException("Unknown principal type: " + principal.getClass());
        }

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Member not found with email: " + email));

        Video video = Video.builder()
                .videoUrl(videoCreateRequest.getVideoUrl())
                .thumbnailUrl(videoCreateRequest.getThumbnailUrl())
                .member(member)
                .build();

        return VideoCreateResponse.from(videoRepository.save(video));
    }


}