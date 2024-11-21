package com.trip.tripshorts.video.dto;

import com.trip.tripshorts.member.domain.Member;
import com.trip.tripshorts.tour.domain.Tour;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VideoCreateRequest {
    private String videoUrl;
    private String thumbnailUrl;
    private Long tourId;
}