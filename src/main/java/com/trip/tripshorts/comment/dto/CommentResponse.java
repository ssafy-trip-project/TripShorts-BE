package com.trip.tripshorts.comment.dto;

import com.trip.tripshorts.comment.domain.Comment;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentResponse {
    private Long id;
    private String content;
    private String nickname;
    private String userProfileUrl;

    public static CommentResponse from(Comment comment, String presignedUrl){
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .nickname(comment.getMember().getNickname())
                .userProfileUrl(presignedUrl)
                .build();
    }

}
