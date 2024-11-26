package com.trip.tripshorts.member.repository;

import com.trip.tripshorts.member.domain.Member;
import com.trip.tripshorts.video.dto.VideoListResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
}
