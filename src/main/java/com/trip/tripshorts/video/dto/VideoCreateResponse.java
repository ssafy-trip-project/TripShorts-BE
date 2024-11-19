package com.trip.tripshorts.video.dto;

import com.trip.tripshorts.video.domain.Video;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VideoCreateResponse {
    private Long id;
    private String videoUrl;
    private String thumbnailUrl;
    private Long memberId;
    private Long tourId;
    private int likeCount;
    private int commentCount;

    public static VideoCreateResponse from(Video video) {
        return VideoCreateResponse.builder()
                .id(video.getId())
                .videoUrl(video.getVideoUrl())
                .thumbnailUrl(video.getThumbnailUrl())
                .memberId(video.getMember().getId())
                .likeCount(0)
                .commentCount(0)
                .build();
    }
}
