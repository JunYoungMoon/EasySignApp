package com.member.easysignapp.service;

import com.member.easysignapp.domain.Member;
import com.member.easysignapp.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public Member signUp(String username, String email, String password) {
        // 회원 가입 로직 구현
        // 아이디 중복 체크, 이메일 중복 체크 등의 검증 로직을 추가해야 합니다.
        Member member = new Member();
        member.setUsername(username);
        member.setEmail(email);
        member.setPassword(password);

        return memberRepository.save(member);
    }
}

