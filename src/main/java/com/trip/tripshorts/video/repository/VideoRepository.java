package com.trip.tripshorts.video.repository;

import com.trip.tripshorts.tour.domain.Tour;
import com.trip.tripshorts.video.domain.Video;
import com.trip.tripshorts.video.dto.VideoListResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

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

    @Query("SELECT DISTINCT v FROM Video v " +
            "LEFT JOIN FETCH v.member m " +  // N+1 방지
            "WHERE (:cursorId IS NULL OR v.id < :cursorId) " +
            "ORDER BY v.id DESC " +
            "LIMIT :size")
    List<Video> findVideosByCursorId(@Param("cursorId") Long cursorId, @Param("size") int size);
}
