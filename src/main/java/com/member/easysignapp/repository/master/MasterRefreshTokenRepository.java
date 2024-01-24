package com.member.easysignapp.repository.master;

import com.member.easysignapp.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterRefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    void deleteByToken(String token);
}
