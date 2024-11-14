package com.trip.tripshorts.auth.service;

import com.trip.tripshorts.auth.OAuthAttributes;
import com.trip.tripshorts.auth.domain.UserPrincipal;
import com.trip.tripshorts.member.domain.Member;
import com.trip.tripshorts.member.dto.MemberDto;
import com.trip.tripshorts.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // OAuth2 서비스의 유저 정보들
        Map<String, Object> attributes = oAuth2User.getAttributes();

        MemberDto memberProfile = OAuthAttributes.extract(registrationId, attributes);
        Member member = saveOrUpdate(memberProfile);

        return new UserPrincipal(member, oAuth2User.getAttributes());
    }

    private Member saveOrUpdate(MemberDto memberDto) {
        Member member = memberRepository.findByEmail(memberDto.getEmail())
                .map(m -> m.update(memberDto.getEmail(), memberDto.getNickname()))
                .orElse(memberDto.toEntity());

        return memberRepository.save(member);
    }
}