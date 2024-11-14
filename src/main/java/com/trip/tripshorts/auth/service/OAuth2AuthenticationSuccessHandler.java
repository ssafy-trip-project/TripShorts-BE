package com.trip.tripshorts.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trip.tripshorts.auth.JwtTokenProvider;
import com.trip.tripshorts.auth.domain.LoginResponse;
import com.trip.tripshorts.auth.domain.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider tokenProvider;
    private final String REDIRECT_URI = "http://localhost:3000"; // 프론트엔드 URI

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        // JWT 토큰 생성
        String accessToken = tokenProvider.createAccessToken(userPrincipal.getEmail());
        String refreshToken = tokenProvider.createRefreshToken(userPrincipal.getEmail());

        // JSON 응답 생성
        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        // Response 헤더 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // JSON 응답 전송
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(loginResponse);
        response.getWriter().write(jsonResponse);
    }
}
