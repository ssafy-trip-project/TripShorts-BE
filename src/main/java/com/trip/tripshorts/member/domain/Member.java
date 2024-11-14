package com.trip.tripshorts.member.domain;

import com.trip.tripshorts.util.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String imageUrl;
    private String nickname;

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

}