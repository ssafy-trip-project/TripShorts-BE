package com.trip.tripshorts.util;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public String generatePresignedUrlForUpload(String originalFilename, String contentType) {
        String fileKey = "videos/shorts/" + UUID.randomUUID() + "-" + originalFilename;
        log.info("Generating presigned URL for bucket: {}, fileKey: {}", bucket, fileKey);

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, fileKey)
                .withMethod(HttpMethod.PUT)
                .withContentType(contentType);

        // 현재 시간으로부터 10분 후로 만료 시간 설정
        request.setExpiration(Date.from(Instant.now().plus(Duration.ofMinutes(10))));

        return amazonS3.generatePresignedUrl(request).toString();
    }

    public String generatePresignedUrlForDownload(String fileKey) {
        log.info("Generating presigned URL for bucket: {}, fileKey: {}", bucket, fileKey);

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, fileKey)
                .withMethod(HttpMethod.GET);

        // 현재 시간으로부터 60분 후로 만료 시간 설정
        request.setExpiration(Date.from(Instant.now().plus(Duration.ofMinutes(60))));

        return amazonS3.generatePresignedUrl(request).toString();
    }

    public String generatePresignedUrlForImgUpload(String originalFilename, String contentType) {
        String fileKey = "videos/profile/" + UUID.randomUUID() + "-" + originalFilename;
        log.info("Generating presigned URL for bucket: {}, fileKey: {}", bucket, fileKey);

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, fileKey)
                .withMethod(HttpMethod.PUT)
                .withContentType(contentType);

        // 현재 시간으로부터 10분 후로 만료 시간 설정
        request.setExpiration(Date.from(Instant.now().plus(Duration.ofMinutes(10))));

        return amazonS3.generatePresignedUrl(request).toString();
    }

    public File downloadFile(String fileKey, String localPath) throws IOException {
        log.info("Downloading file from bucket: {}, fileKey: {}", bucket, fileKey);

        S3Object s3Object = amazonS3.getObject(bucket, fileKey);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();

        File localFile = new File(localPath);
        File parentDir = localFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (FileOutputStream outputStream = new FileOutputStream(localFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            inputStream.close();
        }

        log.info("File downloaded successfully: {}", localFile.getAbsolutePath());
        return localFile;
    }

    public String uploadFile(InputStream inputStream, String filename, long contentLength, String contentType) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(contentLength);

        amazonS3.putObject(bucket, filename, inputStream, metadata);

        return amazonS3.getUrl(bucket, filename).toString();
    }

    public void uploadHlsFiles(String localDir, String s3Prefix) throws IOException {
        File folder = new File(localDir);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IOException("HLS 변환 폴더가 존재하지 않습니다: " + localDir);
        }

        for (File file : folder.listFiles()) {
            String key = s3Prefix + file.getName(); // S3 저장 경로
            amazonS3.putObject(bucket, key, file);
            log.info("HLS 파일 업로드 완료: {}", key);
        }
    }

}