package com.trip.tripshorts.comment.dto;

import com.trip.tripshorts.comment.domain.Comment;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentResponse {
    private Long id;
    private String content;
    private String nickname;
    private String userProfileUrl;
    private LocalDateTime createdAt;

    public static CommentResponse from(Comment comment){
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .nickname(comment.getMember().getNickname())
                .userProfileUrl(comment.getMember().getImageUrl())
                .createdAt(comment.getCreatedDate())
                .build();
    }

}
