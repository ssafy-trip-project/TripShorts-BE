package com.trip.tripshorts.video.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public String generatePresignedUrl(String originalFilename, String contentType) {
        String fileKey = "videos/shorts/" + UUID.randomUUID() + "-" + originalFilename;
        log.info("Generating presigned URL for bucket: {}, fileKey: {}", bucket, fileKey);

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, fileKey)
                .withMethod(HttpMethod.PUT)
                .withContentType(contentType);

        // 현재 시간으로부터 10분 후로 만료 시간 설정
        request.setExpiration(Date.from(Instant.now().plus(Duration.ofMinutes(10))));

        return amazonS3.generatePresignedUrl(request).toString();
    }
}