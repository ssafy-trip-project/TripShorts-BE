package com.trip.tripshorts.video.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public String generatePresignedUrl(String originalFilename, String contentType) {
        // 파일 경로 생성 (videos/shorts/UUID-원본파일명)
        String fileKey = "videos/shorts/" + UUID.randomUUID() + "-" + originalFilename;

        // presigned URL 생성을 위한 객체 생성
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, fileKey)
                .withMethod(HttpMethod.PUT)
                .withContentType(contentType);

        // URL 만료 시간 설정 (10분)
        Date expiration = new Date();
        expiration.setTime(expiration.getTime() + 1000 * 60 * 10);
        generatePresignedUrlRequest.setExpiration(expiration);

        // presigned URL 생성 및 반환
        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }
}