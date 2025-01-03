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

    private double lat;
    private double lng;

    @Column(name="area_name")
    private String areaName;
    @Column(name="district_name")
    private String districtName;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Video> videos = new ArrayList<>();
}
