package com.mjy.easysignapp.repository.master;

import com.mjy.easysignapp.entity.SocialMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterSocialMemberRepository extends JpaRepository<SocialMember, Long> {
}
