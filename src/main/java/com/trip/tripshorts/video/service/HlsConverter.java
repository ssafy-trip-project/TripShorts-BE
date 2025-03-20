package com.trip.tripshorts.video.service;

import com.trip.tripshorts.util.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class HlsConverter {

    private final S3Service s3Service;

    @Value("${spring.path.s3}")
    private String s3Path;

    public String convertToHls(MultipartFile videoFile, String outputDir) throws IOException, InterruptedException {
        File outputFolder = new File(outputDir);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        // MultipartFile을 임시 파일로 저장
        File tempFile = File.createTempFile("upload_", ".mp4");
        videoFile.transferTo(tempFile);

        // 변환될 HLS 파일의 UUID 생성
        String fileName = UUID.randomUUID().toString();
        String m3u8MasterPath = outputDir + "/" + fileName + "_master.m3u8";

        // 여러 해상도로 변환
        String[] resolutions = {"640x360", "854x480", "1280x720"};
        String[] bitrates = {"800k", "1400k", "2800k"};
        List<String> hlsVariants = new ArrayList<>();

        for (int i = 0; i < resolutions.length; i++) {
            String resolution = resolutions[i];
            String bitrate = bitrates[i];
            String variantM3U8 = outputDir + "/" + fileName + "_" + (i + 1) + ".m3u8";
            hlsVariants.add("#EXT-X-STREAM-INF:BANDWIDTH=" + bitrate.replace("k", "000") + ",RESOLUTION=" + resolution + "\n" + fileName + "_" + (i + 1) + ".m3u8");

            List<String> command = Arrays.asList(
                    "ffmpeg", "-y", "-i", tempFile.getAbsolutePath(),
                    "-profile:v", "baseline", "-level", "3.0", "-s", resolution, "-b:v", bitrate,
                    "-start_number", "0", "-hls_time", "5", "-hls_list_size", "0",
                    "-f", "hls", variantM3U8
            );

            // FFmpeg 실행
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
        }


        File masterPlaylist = new File(m3u8MasterPath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(masterPlaylist))) {
            writer.write("#EXTM3U\n");
            for (String variant : hlsVariants) {
                writer.write(variant + "\n");
            }
        }

        // 변환 완료 후 임시 파일 삭제
        tempFile.delete();

        // S3 업로드
        s3Service.uploadHlsFiles(outputDir, "videos/hls/");

        log.info("S3 업로드 완료");

        // S3에 저장된 .m3u8 파일의 URL 반환
        return s3Path + "/videos/hls/" + fileName + "_master.m3u8";
    }
}
