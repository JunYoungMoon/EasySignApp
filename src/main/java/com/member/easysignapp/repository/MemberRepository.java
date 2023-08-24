package com.member.easysignapp.repository;

import com.member.easysignapp.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsById(String id);
    Optional<Member> findById(String id);
}
