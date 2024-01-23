package com.member.easysignapp.service;

import com.member.easysignapp.repository.master.MasterMemberRepository;
import com.member.easysignapp.security.SecurityMember;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomMemberDetailsService implements UserDetailsService {
    private final MasterMemberRepository masterMemberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new SecurityMember(masterMemberRepository.findByUuid(username)
                .orElseThrow(() -> new RuntimeException("잘못된 이메일 또는 비밀번호")));
    }
}
