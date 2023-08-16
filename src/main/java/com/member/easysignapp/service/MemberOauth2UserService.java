package com.member.easysignapp.service;

import com.member.easysignapp.domain.MemberSocial;
import com.member.easysignapp.repository.MemberSocialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class MemberOauth2UserService extends DefaultOAuth2UserService {
    private final MemberSocialRepository memberSocialRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttributes : {}", oAuth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = oAuth2User.getAttribute("sub");
        String Id = provider + "_" +providerId;

        Optional<MemberSocial> optionalUser = memberSocialRepository.findById(Id);
        MemberSocial memberSocial;

        if(optionalUser.isEmpty()) {
            memberSocial = MemberSocial.builder()
                    .id(Id)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            memberSocialRepository.save(memberSocial);
        } else {
            memberSocial = optionalUser.get();
        }

        return oAuth2User;
    }
}
