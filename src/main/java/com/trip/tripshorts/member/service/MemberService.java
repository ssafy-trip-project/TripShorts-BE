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

    public MemberDto getMyprofile(Long id) {
        Member m = authService.getCurrentMember();
        boolean isMy = id == null || m.getId().equals(id);

        Member member = memberRepository.findById(id == null ? authService.getCurrentMember().getId() : id)
                        .orElseThrow();

        return MemberDto.from(member, isMy);
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
