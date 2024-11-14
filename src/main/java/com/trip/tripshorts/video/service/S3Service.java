package com.trip.tripshorts.video.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

        try {
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, fileKey)
                    .withMethod(HttpMethod.PUT)
                    .withContentType(contentType);

            Date expiration = new Date();
            expiration.setTime(expiration.getTime() + 1000 * 60 * 10); // 10분
            generatePresignedUrlRequest.setExpiration(expiration);

            String url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
            log.info("Generated URL: {}", url);
            return url;

        } catch (Exception e) {
            log.error("Error generating presigned URL: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }
}