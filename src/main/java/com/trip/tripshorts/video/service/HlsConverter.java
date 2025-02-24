package com.trip.tripshorts.video.service;

import java.io.File;
import java.io.IOException;

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
        process.waitFor();
    }
}
