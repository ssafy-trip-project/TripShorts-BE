package com.trip.tripshorts.video.dto;

import com.trip.tripshorts.tour.domain.Tour;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VideoInfoResponse {
    private Long videoId;
    private Long tourId;
    private String title;
    private String address;
    private double lat;
    private double lng;

    public VideoInfoResponse(Long videoId, Tour tour) {
        this.videoId = videoId;
        this.tourId = tour.getId();
        this.title = tour.getTitle();
        this.address = tour.getAddress();
        this.lat = tour.getLat();
        this.lng = tour.getLng();
    }
}
