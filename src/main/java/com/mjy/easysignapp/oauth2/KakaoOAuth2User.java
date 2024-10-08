package com.mjy.easysignapp.oauth2;

import java.util.Map;

public class KakaoOAuth2User extends OAuth2UserInfo {

    private final Long id;

    public KakaoOAuth2User(Map<String, Object> attributes) {
        super((Map<String, Object>) attributes.get("kakao_account"));
        this.id = (Long) attributes.get("id");
    }

    @Override
    public String getOAuth2Id() {
        return this.id.toString();
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) ((Map<String, Object>) attributes.get("profile")).get("nickname");
    }

    @Override
    public String getProfileImage() {
        return (String) ((Map<String, Object>) attributes.get("profile")).get("thumbnail_image_url");
    }
}
