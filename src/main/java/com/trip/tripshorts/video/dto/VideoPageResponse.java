package com.trip.tripshorts.video.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class VideoPageResponse {
    private final List<VideoResponse> videos;
    private final Long nextCursor;
    private final boolean hasNext;

    public static VideoPageResponse of(List<VideoResponse> videos, Long lastVideoId, boolean hasNext) {
        return VideoPageResponse.builder()
                .videos(videos)
                .nextCursor(hasNext ? lastVideoId : null)
                .hasNext(hasNext)
                .build();
    }
}
