package com.trip.tripshorts.video.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class VideoPageResponse {
    private final VideoResponse currentVideo;
    private final List<Long> previousVideoIds;
    private final List<Long> nextVideoIds;


    public static VideoPageResponse of(VideoResponse currentVideo, List<Long> previousVideoIds, List<Long> nextVideoIds) {
        return VideoPageResponse.builder()
                .currentVideo(currentVideo)
                .previousVideoIds(previousVideoIds)
                .nextVideoIds(nextVideoIds)
                .build();
    }
}
