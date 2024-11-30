package com.trip.tripshorts.comment.service;

import com.trip.tripshorts.auth.service.AuthService;
import com.trip.tripshorts.comment.domain.Comment;
import com.trip.tripshorts.comment.dto.CommentRequest;
import com.trip.tripshorts.comment.dto.CommentResponse;
import com.trip.tripshorts.comment.repository.CommentRepository;
import com.trip.tripshorts.member.domain.Member;
import com.trip.tripshorts.member.repository.MemberRepository;
import com.trip.tripshorts.video.domain.Video;
import com.trip.tripshorts.video.repository.VideoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final MemberRepository memberRepository;
    private final VideoRepository videoRepository;
    private final CommentRepository commentRepository;
    private final AuthService authService;

    public List<CommentResponse> getCommentList(Long videoId) {
        List<CommentResponse> comments = commentRepository.findAllByVideoIdOrderByCreatedDateDesc(videoId).
                stream()
                .map(CommentResponse::from)
                .toList();
        log.debug("getComment service 정상 반응");
        return comments;
    };

    @Transactional
    public void createComment(Long videoId, CommentRequest commentRequest) {
        Member member = authService.getCurrentMember();
        Video video = videoRepository.findById(videoId)
                .orElseThrow(()-> new EntityNotFoundException("Video not found"));
        
        Comment comment = Comment.builder()
                .content(commentRequest.content())
                .video(video)
                .member(member)
                .build();

        member.addComment(comment);
        video.addComment(comment);

        commentRepository.save(comment);
    }
}
