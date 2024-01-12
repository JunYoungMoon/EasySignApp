package com.member.easysignapp.service;

import com.member.easysignapp.entity.Member;
import com.member.easysignapp.entity.SocialMember;
import com.member.easysignapp.enums.AuthProvider;
import com.member.easysignapp.oauth2.OAuth2UserInfo;
import com.member.easysignapp.oauth2.OAuth2UserInfoFactory;
import com.member.easysignapp.repository.MemberRepository;
import com.member.easysignapp.repository.SocialMemberRepository;
import com.member.easysignapp.security.SecurityMember;
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
import java.util.UUID;

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

        return processOAuth2User(userRequest, oAuth2User);
    }

    protected OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        AuthProvider authProvider = AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase());
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(authProvider, oAuth2User.getAttributes());

        Optional<SocialMember> optionalUser = socialMemberRepository.findByProviderId(oAuth2UserInfo.getOAuth2Id());

        SocialMember socialMember;
        Member member;

        if (optionalUser.isEmpty()) {
            socialMember = SocialMember.builder()
                    .provider(authProvider)
                    .providerId(oAuth2UserInfo.getOAuth2Id())
                    .build();

            socialMember = socialMemberRepository.save(socialMember);

            Long socialMemberId = socialMember.getIdx();

            List<String> roles = new ArrayList<>();
            roles.add("user");

            //jwt토큰에 보여질 UUID 생성
            UUID randomUUID = UUID.randomUUID();

            member = Member.builder()
                    .socialIdx(socialMemberId)
                    .uuid(randomUUID.toString())
                    .email(oAuth2UserInfo.getEmail())
                    .name(oAuth2UserInfo.getName())
                    .roles(roles)
                    .profileImage(oAuth2UserInfo.getProfileImage())
                    .build();

            memberRepository.save(member);
        } else {
            socialMember = optionalUser.get();

            Optional<Member> foundMember = memberRepository.findBySocialIdx(socialMember.getIdx());
            if (foundMember.isPresent()) {
                member = foundMember.get();
            } else {
                throw new RuntimeException("Member not found");
            }
        }

        return new SecurityMember(member, oAuth2User.getAttributes());
    }
}
