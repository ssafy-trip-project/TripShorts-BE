package com.trip.tripshorts.good.domain;

import com.trip.tripshorts.member.domain.Member;
import com.trip.tripshorts.video.domain.Video;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Good {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "good_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static Good createGood(){
        return new Good();
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
