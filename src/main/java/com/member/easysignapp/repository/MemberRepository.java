package com.member.easysignapp.repository;

import com.member.easysignapp.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
