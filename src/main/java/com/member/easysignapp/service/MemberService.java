package com.member.easysignapp.service;

import com.member.easysignapp.domain.Member;
import com.member.easysignapp.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member signUp(String username, String email, String password) {
        // 아이디 중복 체크
        if (memberRepository.existsByUsername(username)) {
            throw new RuntimeException("이미 사용중인 아이디입니다.");
        }

        // 이메일 중복 체크
        if (memberRepository.existsByEmail(email)) {
            throw new RuntimeException("이미 사용중인 이메일입니다.");
        }

        Member member = new Member();
        member.setUsername(username);
        member.setEmail(email);
        member.setPassword(password);

        return memberRepository.save(member);
    }
}

