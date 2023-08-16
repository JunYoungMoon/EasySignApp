package com.member.easysignapp.repository;

import com.member.easysignapp.domain.MemberSocial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberSocialRepository extends JpaRepository<MemberSocial, Long> {
    Optional<MemberSocial> findById(String id);
}
