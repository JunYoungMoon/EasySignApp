package com.member.easysignapp.repository;

import com.member.easysignapp.domain.SocialMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialMemberRepository extends JpaRepository<SocialMember, Long> {
    Optional<SocialMember> findById(String id);
}
