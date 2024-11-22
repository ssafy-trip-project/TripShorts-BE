package com.trip.tripshorts.comment.service;

import com.trip.tripshorts.comment.domain.Comment;
import com.trip.tripshorts.comment.dto.CommentResponse;
import com.trip.tripshorts.comment.repository.CommentRepository;
import com.trip.tripshorts.member.domain.Member;
import com.trip.tripshorts.member.repository.MemberRepository;
import com.trip.tripshorts.video.domain.Video;
import com.trip.tripshorts.video.repository.VideoRepository;
import com.trip.tripshorts.video.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final MemberRepository memberRepository;
    private final VideoRepository videoRepository;
    private final CommentRepository commentRepository;
    private final S3Service s3Service;

    public List<CommentResponse> getCommentList(Long videoId) {
        List<CommentResponse> comments = commentRepository.findAllByVideoId(videoId).
                stream()
                .map(comment -> {
                    String presignedUrl = s3Service.generatePresignedUrlForDownload(comment.getMember().getImageUrl());
                    return CommentResponse.from(comment, presignedUrl);
                })
                .toList();
        log.debug("getComment service 정상 반응");
        return comments;
    };
}
