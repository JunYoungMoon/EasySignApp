package com.member.easysignapp.service;


import com.member.easysignapp.entity.RefreshToken;
import com.member.easysignapp.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public void saveRefreshToken(String id, String token, Instant expiryDate) {
        RefreshToken refreshToken =
                RefreshToken.builder()
                .id(id)
                .token(token)
                .expiryDate(expiryDate)
                .build();
        refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}
