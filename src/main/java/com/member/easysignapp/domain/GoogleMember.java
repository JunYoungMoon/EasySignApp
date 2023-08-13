package com.member.easysignapp.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@Entity
public class GoogleMember implements OAuth2User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;

    @Override
    public Map<String, Object> getAttributes() {
        // OAuth2 공급자로부터 받은 사용자 정보를 맵으로 반환
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", getEmail());
        // 다른 사용자 정보도 필요한 경우 추가로 넣어주면 된다.
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getName() {
        // 사용자의 고유 식별자를 반환 (일반적으로 이메일 등)
        return getEmail();
    }
}
