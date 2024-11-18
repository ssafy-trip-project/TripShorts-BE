package com.trip.tripshorts.comment.service;

import com.trip.tripshorts.comment.domain.Comment;
import com.trip.tripshorts.comment.repository.CommentRepository;
import com.trip.tripshorts.member.domain.Member;
import com.trip.tripshorts.member.repository.MemberRepository;
import com.trip.tripshorts.video.domain.Video;
import com.trip.tripshorts.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final MemberRepository memberRepository;
    private final VideoRepository videoRepository;
    private final CommentRepository commentRepository;

    public void saveComment(String email, Long videoId, String content) {

        // 유저 검색
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(RuntimeException::new);

        // 영상 검색
        Video video = videoRepository.findById(videoId)
                .orElseThrow(RuntimeException::new);

        Comment comment = new Comment(content);

        member.addComment(comment);
        video.addComment(comment);

        commentRepository.save(comment);
    }
}
