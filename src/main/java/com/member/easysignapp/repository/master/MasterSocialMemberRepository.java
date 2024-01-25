package com.member.easysignapp.repository.master;

import com.member.easysignapp.entity.SocialMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterSocialMemberRepository extends JpaRepository<SocialMember, Long> {
}
