package com.trip.tripshorts.auth;

import com.trip.tripshorts.auth.JwtTokenProvider;
import com.trip.tripshorts.auth.domain.UserPrincipal;
import com.trip.tripshorts.member.domain.Member;
import com.trip.tripshorts.member.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 토큰 확인
        String accessToken = jwtTokenProvider.resolveAccessToken(request);

        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            String email = jwtTokenProvider.getEmailFromToken(accessToken);

            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            UserPrincipal userPrincipal = new UserPrincipal(member, new HashMap<>());

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userPrincipal,
                    null,
                    userPrincipal.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
