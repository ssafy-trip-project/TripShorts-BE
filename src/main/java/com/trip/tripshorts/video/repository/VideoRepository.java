package com.trip.tripshorts.video.repository;

import com.trip.tripshorts.tour.domain.Tour;
import com.trip.tripshorts.video.domain.Video;
import com.trip.tripshorts.video.dto.VideoListResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findAllByMemberId(Long memberId);

    @Query("SELECT new com.trip.tripshorts.video.dto.VideoListResponse(v.thumbnailUrl, m.nickname, m.imageUrl, SIZE(v.likes)) " +
            "FROM Video v " +
            "JOIN v.member m")
    List<VideoListResponse> fetchVideoList();

    @Query("SELECT t FROM Video v " +
            "JOIN v.tour t " +
            "WHERE v.id = :videoId")
    Optional<Tour> findTourByVideoId(@Param("videoId") Long videoId);
}
