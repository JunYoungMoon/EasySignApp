package com.member.easysignapp.service;

import com.member.easysignapp.repository.slave.SlaveMemberRepository;
import com.member.easysignapp.security.SecurityMember;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomMemberDetailsService implements UserDetailsService {
    private final SlaveMemberRepository slaveMemberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new SecurityMember(slaveMemberRepository.findByUuid(username)
                .orElseThrow(() -> new RuntimeException("잘못된 이메일 또는 비밀번호")));
    }
}
