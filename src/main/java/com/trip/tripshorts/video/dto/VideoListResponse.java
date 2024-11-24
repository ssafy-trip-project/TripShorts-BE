package com.trip.tripshorts.video.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VideoListResponse {
    private String thumbnailUrl;
    private String nickname;
    private String profileImageUrl;
    private int likeCount;
    private Long viewCount;
}
