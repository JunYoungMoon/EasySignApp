package com.member.easysignapp.entity;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Entity
@Table(name = "refresh_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false)
    private String uuid;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    @Builder
    public RefreshToken(Long idx, String uuid, String token, Instant expiryDate) {
        this.idx = idx;
        this.uuid = uuid;
        this.token = token;
        this.expiryDate = expiryDate;
    }
}
