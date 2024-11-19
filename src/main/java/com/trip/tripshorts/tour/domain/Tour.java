package com.trip.tripshorts.tour.domain;

import com.trip.tripshorts.comment.domain.Comment;
import com.trip.tripshorts.video.domain.Video;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tour_id")
    private Long id;

    private String title;
    private String address;

    @Column(name = "area_code")
    private int areaCode;

    @Column(name = "si_gun_gu_code")
    private int siGunGuCode;
    private double lat;
    private double lng;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Video> videos = new ArrayList<>();
}
