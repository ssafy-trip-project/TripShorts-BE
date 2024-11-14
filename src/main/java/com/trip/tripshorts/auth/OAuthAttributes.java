package com.trip.tripshorts.auth;

import com.trip.tripshorts.member.dto.MemberDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public enum OAuthAttributes {
    KAKAO("kakao", attributes -> {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        return new MemberDto((String) profile.get("nickname"), (String) kakaoAccount.get("email"), (String) profile.get("profile_image_url")
        );
    });

    private final String registrationId;
    private final Function<Map<String, Object>, MemberDto> of;

    public static MemberDto extract(String registrationId, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(provider -> registrationId.equals(provider.registrationId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .of.apply(attributes);
    }
}
