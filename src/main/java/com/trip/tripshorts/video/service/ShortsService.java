package com.trip.tripshorts.video.service;

import com.trip.tripshorts.video.domain.Shorts;
import com.trip.tripshorts.video.repository.ShortsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShortsService {

    private final ShortsRepository shortsRepository;
    private final S3Service s3Service;

    public String getPresignedUrl(String filename, String contentType) {
        return s3Service.generatePresignedUrl(filename, contentType);
    }
//    public Shorts createShorts(String videoUrl, String originalFileName, String description, User user)
    public Shorts createShorts(String videoUrl, String originalFileName, String description) {
        Shorts shorts = Shorts.builder()
                .videoUrl(videoUrl)
                .originalFileName(originalFileName)
                .description(description)
//                .user(user)
                .build();

        return shortsRepository.save(shorts);
    }
}