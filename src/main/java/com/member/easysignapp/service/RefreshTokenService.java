package com.member.easysignapp.service;


import com.member.easysignapp.entity.RefreshToken;
import com.member.easysignapp.repository.master.MasterRefreshTokenRepository;
import com.member.easysignapp.repository.slave.SlaveRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final MasterRefreshTokenRepository masterRefreshTokenRepository;
    private final SlaveRefreshTokenRepository slaveRefreshTokenRepository;

    @Transactional(transactionManager = "masterTransactionManager")
    public void saveRefreshToken(String uuid, String token, Instant expiryDate) {
        RefreshToken refreshToken =
                RefreshToken.builder()
                .uuid(uuid)
                .token(token)
                .expiryDate(expiryDate)
                .build();
        masterRefreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true, transactionManager = "slaveTransactionManager")
    public Optional<RefreshToken> findByToken(String token) {
        return slaveRefreshTokenRepository.findByToken(token);
    }

    @Transactional(transactionManager = "masterTransactionManager")
    public void deleteRefreshToken(String token) {
        masterRefreshTokenRepository.deleteByToken(token);
    }
}
