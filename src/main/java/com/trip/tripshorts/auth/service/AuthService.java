package com.trip.tripshorts.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.trip.tripshorts.auth.JwtTokenProvider;
import com.trip.tripshorts.auth.domain.UserPrincipal;
import com.trip.tripshorts.auth.dto.MemberResponse;
import com.trip.tripshorts.auth.dto.TokenResponse;
import com.trip.tripshorts.member.domain.Member;
import com.trip.tripshorts.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
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
        JsonNode userInfo = getUserInfo("Bearer " + kakaoToken);

        String email = userInfo.get("kakao_account").get("email").asText();
        String nickname = userInfo.get("kakao_account").get("profile").get("nickname").asText();
        String imageUrl = userInfo.get("kakao_account").get("profile").get("thumbnail_image_url").asText();

        // 사용자 정보를 바탕으로 JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(email);
        String refreshToken = jwtTokenProvider.createRefreshToken(email);

        // 이메일로 회원 정보 조회
        Optional<Member> existingMember = memberRepository.findByEmail(email);

        // 처음 로그인 회원이면 DB 저장
        if (existingMember.isEmpty()) {
            Member member = Member.builder()
                    .nickname(nickname)
                    .email(email)
                    .imageUrl(imageUrl)
                    .build();

            memberRepository.save(member);
        }

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

    private JsonNode getUserInfo(String accessToken) {
        try {
            return restClient.get()
                    .uri(resourceUri)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch user info", e);
        }
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

    public Member getCurrentMember() {
        String email = getCurrentUserEmail();
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Member not found"));
    }

    public Long getCurrentMemberId() {
        return getCurrentMember().getId();
    }
}
