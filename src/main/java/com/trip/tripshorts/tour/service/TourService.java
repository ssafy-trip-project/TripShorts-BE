package com.trip.tripshorts.tour.service;

import com.trip.tripshorts.tour.domain.Tour;
import com.trip.tripshorts.tour.dto.TourResponse;
import com.trip.tripshorts.tour.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TourService {

    private final TourRepository tourRepository;

    public List<String> getDistinctAreaNames() {
        return tourRepository.findDistinctAreaNames();
    }

    public List<String> getDistrictsByArea(String areaName) {
        return tourRepository.findDistinctDistrictNamesByAreaName(areaName);
    }


    public List<TourResponse> getAttractionsByDistrict(String areaName, String districtName) {
        return tourRepository.findAllByAreaNameAndDistrictName(areaName, districtName)
                .stream().map(TourResponse::from)
                .toList();
    }
}
