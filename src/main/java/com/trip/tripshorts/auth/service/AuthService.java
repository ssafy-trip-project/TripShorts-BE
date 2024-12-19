package com.trip.tripshorts.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.trip.tripshorts.auth.JwtTokenProvider;
import com.trip.tripshorts.auth.domain.UserPrincipal;
import com.trip.tripshorts.auth.dto.KakaoUserInfo;
import com.trip.tripshorts.auth.dto.TokenResponse;
import com.trip.tripshorts.member.domain.Member;
import com.trip.tripshorts.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RestClient restClient = RestClient.create();
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Value("${kakao.client.id}")
    private String clientId;
    @Value("${kakao.client.secret}")
    private String clientSecret;
    @Value("${kakao.redirect.uri}")
    private String redirectUri;
    @Value("${kakao.token.uri}")
    private String tokenUri;
    @Value("${kakao.resource.uri}")
    private String resourceUri;

    public TokenResponse login(String code) {

        // 코드를 통해 카카오 토큰 발급
        String kakaoToken = getAccessToken(code);

        // 토큰을 통해 사용자 정보 발급
        KakaoUserInfo userInfo = getUserInfo(kakaoToken);

        // 사용자 정보를 바탕으로 JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(userInfo.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(userInfo.getEmail());

        // 이메일로 회원 정보 조회
        memberRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> memberRepository.save(createNewMember(userInfo)));

        return new TokenResponse(accessToken, refreshToken);
    }

    private String getAccessToken(String code) {
        try {
            String uri = UriComponentsBuilder.fromUriString(tokenUri)
                    .queryParam("code", code)
                    .queryParam("client_id", clientId)
                    .queryParam("redirect_uri", redirectUri)
                    .queryParam("client_secret", clientSecret)
                    .queryParam("grant_type", "authorization_code")
                    .toUriString();

            // RestClient로 POST 요청 전송
            return restClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .retrieve()
                    .body(JsonNode.class)
                    .get("access_token").asText();
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to get access token", e);
        }
    }

    private KakaoUserInfo getUserInfo(String accessToken) {
        try {
            JsonNode userInfoNode = restClient.get()
                    .uri(resourceUri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .retrieve()
                    .body(JsonNode.class);

            return KakaoUserInfo.builder()
                    .email(userInfoNode.get("kakao_account").get("email").asText())
                    .nickname(userInfoNode.get("kakao_account").get("profile").get("nickname").asText())
                    .imageUrl(userInfoNode.get("kakao_account").get("profile").get("thumbnail_image_url").asText())
                    .build();

        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch user info", e);
        }
    }

    private Member createNewMember(KakaoUserInfo userInfo) {
        return Member.builder()
                .nickname(userInfo.getNickname())
                .email(userInfo.getEmail())
                .imageUrl(userInfo.getImageUrl())
                .build();
    }

    private String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getEmail();
        } else if (principal instanceof String) {
            return (String) principal;
        }
        throw new RuntimeException("Unknown principal type: " + principal.getClass());
    }

    @Transactional(readOnly = true)
    public Member getCurrentMember() {
        return memberRepository.findByEmail(getCurrentUserEmail())
                .orElseThrow(() -> new RuntimeException("Member not found"));
    }
}
