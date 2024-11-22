package com.trip.tripshorts.comment.controller;

import com.trip.tripshorts.auth.domain.UserPrincipal;
import com.trip.tripshorts.comment.dto.CommentRequest;
import com.trip.tripshorts.comment.dto.CommentResponse;
import com.trip.tripshorts.comment.service.CommentService;
import com.trip.tripshorts.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shorts")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{videoId}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentList(@PathVariable Long videoId){
        List<CommentResponse> comments = commentService.getCommentList(videoId);
        return ResponseEntity.ok(comments);
    }
}
