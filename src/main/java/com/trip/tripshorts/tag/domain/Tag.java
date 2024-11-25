package com.trip.tripshorts.tag.domain;

import com.trip.tripshorts.video.domain.Video;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="video_id")
    private Video video;

    public void setVideo(Video video) {
        this.video = video;
    }
}
