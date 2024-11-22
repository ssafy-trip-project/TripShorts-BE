package com.trip.tripshorts.good.service;

import com.trip.tripshorts.auth.service.AuthService;
import com.trip.tripshorts.good.domain.Good;
import com.trip.tripshorts.good.repository.GoodRepository;
import com.trip.tripshorts.member.domain.Member;
import com.trip.tripshorts.video.domain.Video;
import com.trip.tripshorts.video.repository.VideoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoodService {
    private final AuthService authService;
    private final VideoRepository videoRepository;
    private final GoodRepository goodRepository;

    public void addGood(Long videoId) {
        Member member = authService.getCurrentMember();
        Video video = videoRepository.findById(videoId)
                .orElseThrow(EntityNotFoundException::new);

        Optional<Good> alreadyGood = goodRepository.findByVideoAndMember(video, member);
        if (alreadyGood.isPresent()) {
            throw new RuntimeException("이미 좋아요를 누른 영상입니다");
        }

        Good good = Good.createGood();

        video.addLike(good);
        member.addLike(good);
        goodRepository.save(good);
    }

    public void removeGood(Long videoId) {
        Member member = authService.getCurrentMember();
        Video video = videoRepository.findById(videoId)
                .orElseThrow(EntityNotFoundException::new);

        Good good = goodRepository.findByVideoAndMember(video, member)
                .orElseThrow(EntityNotFoundException::new);

        video.removeLike(good);
        member.removeLike(good);
        goodRepository.delete(good);
    }
}
