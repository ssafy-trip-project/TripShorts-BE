package com.trip.tripshorts.video.dto;

import com.trip.tripshorts.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VideoCreatorDto {
    private Long id;
    private String nickname;
    private String imageUrl;

    public static VideoCreatorDto from(Member member){
        return VideoCreatorDto.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .imageUrl(member.getImageUrl())
                .build();
    }
}
