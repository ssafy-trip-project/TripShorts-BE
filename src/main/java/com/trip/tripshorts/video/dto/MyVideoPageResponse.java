package com.trip.tripshorts.video.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyVideoPageResponse {
    private final List<MyVideoListResponse> videos;
    private final Long nextCursor;
    private final boolean hasNext;

    public static MyVideoPageResponse of(List<MyVideoListResponse> videos, Long lastVideoId, boolean hasNext) {
        return MyVideoPageResponse.builder()
                .videos(videos)
                .nextCursor(hasNext ? lastVideoId : null)
                .hasNext(hasNext)
                .build();
    }
}
