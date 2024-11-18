package com.trip.tripshorts.member.domain;

import com.trip.tripshorts.util.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
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