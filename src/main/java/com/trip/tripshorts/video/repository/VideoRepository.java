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

    @Query("SELECT new com.trip.tripshorts.video.dto.VideoListResponse(v.id, v.thumbnailUrl, m.nickname, m.imageUrl, SIZE(v.likes), v.viewCount) " +
            "FROM Video v " +
            "JOIN v.member m " +
            "ORDER BY v.createdDate DESC")
    List<VideoListResponse> findAllOrderByCreatedDateDesc();

    @Query("SELECT new com.trip.tripshorts.video.dto.VideoListResponse(v.id, v.thumbnailUrl, m.nickname, m.imageUrl, SIZE(v.likes), v.viewCount) " +
            "FROM Video v " +
            "JOIN v.member m " +
            "ORDER BY SIZE(v.likes) DESC")
    List<VideoListResponse> findAllOrderByLikesDesc();

    // 최신순 정렬
    @Query("""
        SELECT v 
        FROM Video v 
        WHERE v.createdDate <= (SELECT v2.createdDate FROM Video v2 WHERE v2.id = :cursorId) 
        ORDER BY v.createdDate DESC
        LIMIT :size
    """)
    List<Video> findTopByRecent(@Param("cursorId") Long cursorId, @Param("size") int size);

    @Query("""
        SELECT v FROM Video v 
        WHERE 
            SIZE(v.likes) < (SELECT SIZE(v2.likes) FROM Video v2 WHERE v2.id = :cursorId) 
            OR (
                SIZE(v.likes) = (SELECT SIZE(v2.likes) FROM Video v2 WHERE v2.id = :cursorId)
                AND v.viewCount <= (SELECT v2.viewCount FROM Video v2 WHERE v2.id = :cursorId)
            )
        ORDER BY SIZE(v.likes) DESC, v.viewCount DESC
        LIMIT :size
    """)
    List<Video> findTopByLikes(@Param("cursorId") Long cursorId, @Param("size") int size);

    @Query("""
        SELECT v 
        FROM Video v 
        WHERE v.viewCount <= (SELECT v2.viewCount FROM Video v2 WHERE v2.id = :cursorId) 
        ORDER BY v.viewCount DESC
        LIMIT :size
    """)
    List<Video> findTopByViews(@Param("cursorId") Long cursorId, @Param("size") int size);

    @Query("SELECT new com.trip.tripshorts.video.dto.VideoListResponse(v.id, v.thumbnailUrl, m.nickname, m.imageUrl, SIZE(v.likes), v.viewCount) " +
            "FROM Video v " +
            "JOIN v.member m " +
            "ORDER BY v.viewCount DESC")
    List<VideoListResponse> findAllOrderByViewCountDesc();

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

    @Query("SELECT DISTINCT v FROM Video v " +
            "LEFT JOIN FETCH v.member m " +
            "LEFT JOIN FETCH v.tour t " +
            "WHERE v.member.id = :memberId " +
            "AND (:cursorId IS NULL OR v.id < :cursorId) " +
            "ORDER BY v.id DESC " +
            "LIMIT :size")
    List<Video> findMyVideosByCursor(
            @Param("memberId") Long memberId,
            @Param("cursorId") Long cursorId,
            @Param("size") int size
    );

    @Query("""
        SELECT v FROM Video v
        LEFT JOIN FETCH v.member m
        LEFT JOIN FETCH v.tour t
        WHERE v.member.id = :memberId
        ORDER BY v.createdDate DESC
        """)
    List<Video> findAllByMemberIdWithDetails(@Param("memberId") Long memberId);

    @Query("SELECT new com.trip.tripshorts.video.dto.VideoListResponse(v.id, v.thumbnailUrl, m.nickname, m.imageUrl, SIZE(v.likes), v.viewCount) " +
            "FROM Video v " +
            "JOIN v.member m " +
            "WHERE m.id = :memberId " +
            "ORDER BY v.createdDate DESC"
    )
    List<VideoListResponse> findVideosById(@Param("memberId") Long memberId);
}
