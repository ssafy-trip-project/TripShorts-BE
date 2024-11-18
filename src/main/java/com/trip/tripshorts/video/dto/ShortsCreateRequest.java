package com.trip.tripshorts.video.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShortsCreateRequest {
    private String videoUrl;
    private String originalFileName;
    private String description;
}