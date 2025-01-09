package com.trip.tripshorts.video.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class VideoPageResponse {
    private final VideoResponse currentVideo;
    private final List<VideoResponse> previousVideos;
    private final List<VideoResponse> nextVideos;


    public static VideoPageResponse of(VideoResponse currentVideo, List<VideoResponse> previousVideos, List<VideoResponse> nextVideos) {
        return VideoPageResponse.builder()
                .currentVideo(currentVideo)
                .previousVideos(previousVideos)
                .nextVideos(nextVideos)
                .build();
    }
}
