package com.trip.tripshorts.comment.controller;

import com.trip.tripshorts.auth.domain.UserPrincipal;
import com.trip.tripshorts.comment.dto.CommentRequest;
import com.trip.tripshorts.comment.service.CommentService;
import com.trip.tripshorts.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shorts")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/videoId")
    public ResponseEntity<Void> saveComment(@RequestParam("videoId") Long videoId,
                                            @RequestBody CommentRequest commentRequest,
                                            @AuthenticationPrincipal UserPrincipal userInfo) {

        commentService.saveComment(userInfo.getEmail(), videoId, commentRequest.content());

        return ResponseEntity.ok().build();

    }
}
