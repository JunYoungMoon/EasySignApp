package com.member.easysignapp.service;

import com.member.easysignapp.domain.Member;
import com.member.easysignapp.domain.SocialMember;
import com.member.easysignapp.repository.MemberRepository;
import com.member.easysignapp.repository.SocialMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final SocialMemberRepository socialMemberRepository;
    private final MemberRepository memberRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttributes : {}", oAuth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = oAuth2User.getAttribute("sub");
        String Id = provider + "_" +providerId;

        Optional<SocialMember> optionalUser = socialMemberRepository.findById(Id);
        SocialMember socialMember;
        Member member;

        if(optionalUser.isEmpty()) {
            socialMember = SocialMember.builder()
                    .id(Id)
                    .provider(provider)
                    .providerId(providerId)
                    .build();

            try {
                socialMemberRepository.save(socialMember);
            } catch (DataAccessException e) {
                throw new RuntimeException("Failed to save member social: " + e.getMessage(), e);
            }

            List<String> roles = new ArrayList<>();
            roles.add("user");

            member = Member.builder()
                    .id(Id)
                    .email(oAuth2User.getAttribute("email"))
                    .roles(roles)
                    .build();

            try {
                memberRepository.save(member);
            } catch (DataAccessException e) {
                throw new RuntimeException("Failed to save member: " + e.getMessage(), e);
            }
        }

        return oAuth2User;
    }

}
