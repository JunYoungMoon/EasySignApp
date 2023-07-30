package com.member.easysignapp.repository;

import com.member.easysignapp.domain.Member;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class MemoryMemberRepository implements MemberRepository {
    private final Map<Long, Member> memberMap = new HashMap<>();
    private long sequence = 0L;

    @Override
    public Member save(Member member) {
        member.setId(++sequence);
        memberMap.put(member.getId(), member);
        return member;
    }

    @Override
    public Member findById(Long id) {
        return memberMap.get(id);
    }

    @Override
    public Member findByUsername(String username) {
        return memberMap.values()
                .stream()
                .filter(member -> member.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Member findByEmail(String email) {
        return memberMap.values()
                .stream()
                .filter(member -> member.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    // 기타 필요한 메서드 추가
}

