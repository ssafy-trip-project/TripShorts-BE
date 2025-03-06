package com.trip.tripshorts.video.service;

import com.trip.tripshorts.util.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class HlsConverter {

    private final S3Service s3Service;

    public String convertToHls(MultipartFile videoFile, String outputDir) throws IOException, InterruptedException {
        File outputFolder = new File(outputDir);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        // MultipartFile을 임시 파일로 저장
        File tempFile = File.createTempFile("upload_", ".mp4");
        videoFile.transferTo(tempFile);

        // Fmpeg 명령어 (pipe:0 대신 파일 경로 사용)
        List<String> command = Arrays.asList(
                "ffmpeg", "-y", "-i", tempFile.getAbsolutePath(), "-profile:v", "baseline", "-level", "3.0",
                "-s", "640x360", "-start_number", "0", "-hls_time", "5", "-hls_list_size", "0",
                "-f", "hls", outputDir + "/index.m3u8"
        );

        // FFmpeg 프로세스 실행
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // FFmpeg 로그 출력
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg 변환 실패: 종료 코드 " + exitCode);
        }

        // 변환 완료 후 임시 파일 삭제
        tempFile.delete();

        // S3 업로드
        s3Service.uploadHlsFiles(outputDir, "videos/shorts/");

        log.info("S3 업로드 완료");

        // S3에 저장된 .m3u8 파일의 URL 반환
        return "https://tripshorts.s3.ap-northeast-2.amazonaws.com/videos/shorts/index.m3u8";
    }
}
