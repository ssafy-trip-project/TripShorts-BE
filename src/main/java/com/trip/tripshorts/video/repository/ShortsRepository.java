package com.trip.tripshorts.video.repository;

import com.trip.tripshorts.video.domain.Shorts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShortsRepository extends JpaRepository<Shorts, Long> {
    List<Shorts> findAllByOrderByCreatedAtDesc();
//    추후 로그인 구현 후 세팅
//    List<Shorts> findByUserOrderByCreatedAtDesc(User user);
}
