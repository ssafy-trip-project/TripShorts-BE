package com.trip.tripshorts.video.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class HlsConverter {

    public static void convertToHls(String inputFilePath, String outputDir) throws IOException, InterruptedException {
        File outputFolder = new File(outputDir);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        String command = String.format(
                "ffmpeg -i %s -profile:v baseline -level 3.0 -s 640x360 -start_number 0 -hls_time 5 -hls_list_size 0 -f hls %s/index.m3u8",
                inputFilePath, outputDir
        );

        Process process = Runtime.getRuntime().exec(command);

        // FFmpeg 출력 확인
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // FFmpeg 오류 확인
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.err.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        process.waitFor();
    }
}
