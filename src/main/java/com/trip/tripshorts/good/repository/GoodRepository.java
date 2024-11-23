package com.trip.tripshorts.good.repository;

import com.trip.tripshorts.good.domain.Good;
import com.trip.tripshorts.member.domain.Member;
import com.trip.tripshorts.video.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoodRepository extends JpaRepository<Good, Long> {
    Optional<Good> findByVideoAndMember(Video video, Member member);

    boolean existsByVideoAndMember(Video video, Member member);
}
