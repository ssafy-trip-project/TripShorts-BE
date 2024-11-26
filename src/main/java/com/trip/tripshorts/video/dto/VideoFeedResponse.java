package com.trip.tripshorts.video.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VideoFeedResponse {
    private final List<VideoResponse> videos;

    public static VideoFeedResponse of(List<VideoResponse> videos) {
        return new VideoFeedResponse(videos);
    }
}
