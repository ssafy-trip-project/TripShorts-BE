package com.trip.tripshorts.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoUserInfo {
    private final String email;
    private final String nickname;
    private final String imageUrl;
}
