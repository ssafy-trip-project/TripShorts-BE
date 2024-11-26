package com.trip.tripshorts.member.controller;

import com.trip.tripshorts.auth.domain.UserPrincipal;
import com.trip.tripshorts.member.dto.MemberDto;
import com.trip.tripshorts.member.dto.MemberImageUrl;
import com.trip.tripshorts.member.service.MemberService;
import com.trip.tripshorts.video.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final S3Service s3Service;

    @GetMapping("/profile")
    public ResponseEntity<MemberDto> getMyProfile(@RequestParam(value = "id", required = false) Long id) {
        return ResponseEntity.ok().body(memberService.getMyprofile(id));
    }

    @GetMapping("/profile/presigned-url")
    public ResponseEntity<Map<String, String>> getPresignedUrlForUpload(
            @RequestParam String filename,
            @RequestParam String contentType) {
        String presignedUrl = s3Service.generatePresignedUrlForImgUpload(filename, contentType);
        log.info(presignedUrl);
        Map<String, String> response = new HashMap<>();
        response.put("presignedUrl", presignedUrl);
        response.put("filename", filename);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile/image")
    public ResponseEntity<Void> modifyImage(@RequestBody MemberImageUrl memberImageUrl) {
        memberService.modifyImage(memberImageUrl.imageUrl());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/profile")
    public ResponseEntity<Void> modifyNickname(@RequestParam("nickname") String nickname) {
        memberService.modifyNickname(nickname);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/leave")
    public ResponseEntity<Void> leave() {
        memberService.leave();
        return ResponseEntity.ok().build();
    }

}
