package com.trip.tripshorts.video.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresignedUrlResponse {
    private String presignedUrl;
    private String filename;
}
