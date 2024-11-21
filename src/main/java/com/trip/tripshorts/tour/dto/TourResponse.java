package com.trip.tripshorts.tour.dto;

import com.trip.tripshorts.tour.domain.Tour;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TourResponse {
    private Long tourId;
    private String title;
    private String address;
    private double lat;
    private double lng;
    private String areaName;
    private String districtName;

    public static TourResponse from(Tour tour) {
        return TourResponse.builder()
                .tourId(tour.getId())
                .title(tour.getTitle())
                .address(tour.getAddress())
                .lat(tour.getLat())
                .lng(tour.getLng())
                .areaName(tour.getAreaName())
                .districtName(tour.getDistrictName())
                .build();
    }
}
