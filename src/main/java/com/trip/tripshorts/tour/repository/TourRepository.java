package com.trip.tripshorts.tour.repository;

import com.trip.tripshorts.tour.domain.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
    @Query("SELECT DISTINCT t.areaName FROM Tour t ORDER BY t.areaName")
    List<String> findDistinctAreaNames();

    @Query("SELECT DISTINCT t.districtName FROM Tour t WHERE t.areaName = :areaName ORDER BY t.districtName")
    List<String> findDistinctDistrictNamesByAreaName(@Param("areaName") String areaName);

    List<Tour> findAllByAreaNameAndDistrictName(String areaName, String districtName);

}
