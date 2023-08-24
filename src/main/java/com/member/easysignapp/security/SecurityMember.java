package com.member.easysignapp.security;

import com.member.easysignapp.entity.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SecurityMember implements UserDetails, OAuth2User {
    private Member member;
    private Long idx;
    private String id;
    private String email;
    private String password;
    private List<String> roles;
    private Map<String, Object> attributes;

    public SecurityMember(Member member) {
        this.idx = member.getIdx();
        this.id = member.getId();
        this.email = member.getEmail();
        this.password = member.getPassword();
        this.roles = member.getRoles();
    }

    public SecurityMember(Member member, Map<String, Object> attributes ) {
        this.idx = member.getIdx();
        this.id = member.getId();
        this.email = member.getEmail();
        this.password = member.getPassword();
        this.roles = member.getRoles();
        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
//                .map(role -> new SimpleGrantedAuthority(role))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public String getUsername() {
        return id;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
