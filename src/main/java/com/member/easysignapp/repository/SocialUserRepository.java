package com.member.easysignapp.repository;

import com.member.easysignapp.domain.SocialUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialUserRepository extends JpaRepository<SocialUser, Long> {
    Optional<SocialUser> findById(String id);
}
