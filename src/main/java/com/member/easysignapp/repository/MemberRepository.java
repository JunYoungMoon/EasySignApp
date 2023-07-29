package com.member.easysignapp.repository;

import com.member.easysignapp.domain.Member;

public interface MemberRepository {
    Member save(Member member);
    Member findById(Long id);
    Member findByUsername(String username);
    Member findByEmail(String email);
    // 기타 필요한 메서드 추가
}
