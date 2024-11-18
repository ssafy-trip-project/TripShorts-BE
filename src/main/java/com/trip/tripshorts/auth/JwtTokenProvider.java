package com.trip.tripshorts.auth;

import com.trip.tripshorts.auth.domain.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private String secretKey = "tokensecretkeytokensecretkeytokensecretkeytokensecretkeytokensecretkey";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7; // 7일

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createAccessToken(String email) {
        return createToken(email, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public String createRefreshToken(String email) {
        return createToken(email, REFRESH_TOKEN_EXPIRE_TIME);
    }

    private String createToken(String email, long expireTime) {
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expireTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String resolveAccessToken(HttpServletRequest request) {
        if(request.getHeader("authorization") != null )
            return request.getHeader("authorization").substring(7);
        return null;
    }

    // 토큰에서 회원 정보 추출
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
