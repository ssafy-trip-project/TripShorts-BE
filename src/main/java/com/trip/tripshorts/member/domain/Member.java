package com.trip.tripshorts.member.domain;

import com.trip.tripshorts.comment.domain.Comment;
import com.trip.tripshorts.good.domain.Good;
import com.trip.tripshorts.util.BaseTimeEntity;
import com.trip.tripshorts.video.domain.Video;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String email;
    private String imageUrl;
    private String nickname;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Video> videos = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Good> likes = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Member(String nickname, String email, String imageUrl) {
        this.nickname = nickname;
        this.email = email;
        this.imageUrl = imageUrl;
    }

    public Member update(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
        return this;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setMember(this);
    }
}