package com.trip.tripshorts.video.repository;

import com.trip.tripshorts.video.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
}
