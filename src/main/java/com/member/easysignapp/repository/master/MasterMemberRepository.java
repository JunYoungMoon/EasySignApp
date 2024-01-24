package com.member.easysignapp.repository.master;

import com.member.easysignapp.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterMemberRepository extends JpaRepository<Member, Long> {
}
