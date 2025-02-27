package com.trip.tripshorts.video.service;

import com.trip.tripshorts.util.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Component
public class HlsConverter {

    private final S3Service s3Service;

    public void convertToHls(MultipartFile videoFile, String outputDir) throws IOException, InterruptedException {
        File outputFolder = new File(outputDir);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        // 🚀 MultipartFile을 임시 파일로 저장
        File tempFile = File.createTempFile("upload_", ".mp4");
        videoFile.transferTo(tempFile);

        // ✅ FFmpeg 명령어 (파일 저장 없이 stdout으로 변환)
        List<String> command = Arrays.asList(
                "ffmpeg", "-y", "-i", tempFile.getAbsolutePath(), "-profile:v", "baseline", "-level", "3.0",
                "-s", "640x360", "-start_number", "0", "-hls_time", "5", "-hls_list_size", "0",
                "-f", "hls", "-hls_segment_filename", "pipe:1",
                "pipe:1"
        );

        // FFmpeg 프로세스 실행
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // ✅ FFmpeg의 stdout을 읽어서 바로 S3에 업로드
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            ByteArrayOutputStream m3u8Buffer = new ByteArrayOutputStream();
            ByteArrayOutputStream tsBuffer = new ByteArrayOutputStream();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#EXT")) { // ✅ m3u8 파일 내용이면
                    m3u8Buffer.write(line.getBytes(StandardCharsets.UTF_8));
                } else { // ✅ ts 파일이면
                    tsBuffer.write(line.getBytes(StandardCharsets.UTF_8));
                }
            }

            // ✅ S3에 업로드
            s3Service.uploadFile(new ByteArrayInputStream(m3u8Buffer.toByteArray()),
                    "videos/index.m3u8",
                    m3u8Buffer.size(),
                    "application/vnd.apple.mpegurl");

            s3Service.uploadFile(new ByteArrayInputStream(tsBuffer.toByteArray()),
                    "videos/segment.ts",
                    tsBuffer.size(),
                    "video/MP2T");
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg 변환 실패: 종료 코드 " + exitCode);
        }

        // ✅ 변환 완료 후 임시 파일 삭제
        tempFile.delete();

        System.out.println("HLS 변환 완료");
    }
}
