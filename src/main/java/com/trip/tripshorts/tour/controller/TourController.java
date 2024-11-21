package com.trip.tripshorts.tour.controller;

import com.trip.tripshorts.tour.dto.TourResponse;
import com.trip.tripshorts.tour.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tour")
public class TourController {

    private final TourService tourService;

    @GetMapping("/areas")
    public ResponseEntity<List<String>> getAreaNames() {
        List<String> areas = tourService.getDistinctAreaNames();
        return ResponseEntity.ok(areas);
    }

    @GetMapping("/districts")
    public ResponseEntity<List<String>> getDistrictNames(@RequestParam String areaName) {
        List<String> districts = tourService.getDistrictsByArea(areaName);
        return ResponseEntity.ok(districts);
    }

    @GetMapping("/attractions")
    public ResponseEntity<List<TourResponse>> getAttractionsByDistrict(
            @RequestParam String areaName,
            @RequestParam String districtName) {
        List<TourResponse> tourInfos = tourService.getAttractionsByDistrict(areaName, districtName);
        return ResponseEntity.ok(tourInfos);
    }


}
