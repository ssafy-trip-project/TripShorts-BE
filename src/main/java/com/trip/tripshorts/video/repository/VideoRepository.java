package com.trip.tripshorts.video.repository;

import com.trip.tripshorts.video.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findAllByMemberId(Long memberId);
}
