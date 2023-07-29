package com.member.easysignapp.service;

import com.member.easysignapp.domain.Member;

public interface MemberService {
    Member signUp(String username, String email, String password);
    // 기타 필요한 메서드 추가
}

