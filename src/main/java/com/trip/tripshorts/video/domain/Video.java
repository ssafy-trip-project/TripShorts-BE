package com.trip.tripshorts.video.domain;

import com.trip.tripshorts.comment.domain.Comment;
import com.trip.tripshorts.good.domain.Good;
import com.trip.tripshorts.member.domain.Member;
import com.trip.tripshorts.tag.domain.Tag;
import com.trip.tripshorts.tour.domain.Tour;
import com.trip.tripshorts.util.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Video extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id")
    private Long id;

    private String videoUrl;
    private String thumbnailUrl;

    @Builder.Default
    private Long viewCount = 0L;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Good> likes = new ArrayList<>();

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id")
    private Tour tour;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tags = new ArrayList<>();

    public void addTags(List<String> tagNames){
        tagNames.forEach(tagName -> {
            Tag tag = Tag.builder()
                    .name(tagName)
                    .video(this)
                    .build();
            this.tags.add(tag);
        });
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void addLike(Good good){
        this.likes.add(good);
        good.setVideo(this);
    }

    public void removeLike(Good good) {
        this.likes.remove(good);
        good.setMember(null);
    }

    public void raiseView() {
        this.viewCount++;
    }
}
