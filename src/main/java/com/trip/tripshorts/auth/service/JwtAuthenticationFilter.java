package com.trip.tripshorts.auth.service;

import com.trip.tripshorts.auth.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 토큰 확인
        String accessToken = jwtTokenProvider.resolveAccessToken(request);

        if (accessToken != null) {
            // 토큰 값이 유효하면 검증
            if (jwtTokenProvider.validateToken(accessToken)) {
                // 토큰에서 이메일 추출
                String email = jwtTokenProvider.getEmailFromToken(accessToken);

                // UserDetails 객체 생성
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                // Authentication 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // SecurityContext 에 인증 객체 등록
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }else{
                // accessToken 값 만료
                System.out.println("오류오류오류오류오류오류오류오류");
            }
        }

        filterChain.doFilter(request, response);
    }
}
