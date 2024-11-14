package com.trip.tripshorts.member.dto;

import com.trip.tripshorts.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberDto {
    private String email;
    private String nickname;
    private String imageUrl;

    public Member toEntity() {
        return Member.builder()
                .email(email)
                .nickname(nickname)
                .imageUrl(imageUrl)
                .build();
    }
}
