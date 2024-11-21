package com.trip.tripshorts.member.service;

import com.trip.tripshorts.auth.service.AuthService;
import com.trip.tripshorts.member.domain.Member;
import com.trip.tripshorts.member.dto.MemberDto;
import com.trip.tripshorts.member.dto.MemberImageUrl;
import com.trip.tripshorts.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthService authService;

    public MemberDto getMyprofile() {
        Member m = authService.getCurrentMember();
        return MemberDto.from(authService.getCurrentMember());
    }

    public void modifyImage(String imageUrl) {
        Member member = authService.getCurrentMember();
        member.setImageUrl(imageUrl);
        memberRepository.save(member);
    }

    public void modifyNickname(String nickname) {
        Member member = authService.getCurrentMember();
        member.setNickname(nickname);
        memberRepository.save(member);
    }

    public void leave() {
        Member member = authService.getCurrentMember();
        memberRepository.delete(member);
    }
}
