package com.trip.tripshorts.comment.domain;

import com.trip.tripshorts.member.domain.Member;
import com.trip.tripshorts.util.BaseTimeEntity;
import com.trip.tripshorts.video.domain.Video;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Comment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public Comment(String content) {
        this.content = content;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setVideo(Video video) {
        this.video = video;
    }
}
