package com.trip.tripshorts.video.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MyVideoListResponse {
    private Long videoId;
    private String thumbnailUrl;
    private String tourName;
}
