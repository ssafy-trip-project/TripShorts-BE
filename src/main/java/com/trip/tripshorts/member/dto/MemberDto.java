package com.trip.tripshorts.member.dto;

import com.trip.tripshorts.member.domain.Member;
import com.trip.tripshorts.video.domain.Video;
import com.trip.tripshorts.video.dto.VideoCreateResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
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

    public static MemberDto from(Member member) {
        return MemberDto.builder()
                .nickname(member.getNickname())
                .imageUrl(member.getImageUrl())
                .email(member.getEmail())
                .build();
    }
}
