package com.trip.tripshorts.auth.controller;

import com.trip.tripshorts.auth.domain.UserPrincipal;
import com.trip.tripshorts.auth.dto.KakaoAuthCode;
import com.trip.tripshorts.auth.dto.MemberResponse;
import com.trip.tripshorts.auth.dto.TokenResponse;
import com.trip.tripshorts.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/kakao")
    public ResponseEntity<TokenResponse> getToken(@RequestBody KakaoAuthCode kakaoAuthCode) {

        return ResponseEntity.ok().body(authService.login(kakaoAuthCode.code()));
    }

    @GetMapping("/member")
    public ResponseEntity<MemberResponse> getNickname(@AuthenticationPrincipal UserPrincipal userInfo) {

        return ResponseEntity.ok().body(new MemberResponse(userInfo.getName()));
    }
}
