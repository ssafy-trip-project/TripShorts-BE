package com.trip.tripshorts.video.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyVideoPageResponse {
    private final String nickname;
    private final List<MyVideoListResponse> videos;
    private final Long nextCursor;
    private final boolean hasNext;

    public static MyVideoPageResponse of(String nickname, List<MyVideoListResponse> videos, Long lastVideoId, boolean hasNext) {
        return MyVideoPageResponse.builder()
                .nickname(nickname)
                .videos(videos)
                .nextCursor(hasNext ? lastVideoId : null)
                .hasNext(hasNext)
                .build();
    }
}
