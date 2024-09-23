package com.mjy.easysignapp.repository.master;

import com.mjy.easysignapp.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterMemberRepository extends JpaRepository<Member, Long> {
}
