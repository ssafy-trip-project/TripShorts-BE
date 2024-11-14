package com.trip.tripshorts.member.domain;

import com.trip.tripshorts.util.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_Id")
    private Long id;

    private String email;
    private String imageUrl;
    private String nickname;

}