package com.member.easysignapp.entity;

import com.member.easysignapp.enums.AuthProvider;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    @Column(nullable = false)
    private String id;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;
    @Column(nullable = false)
    private String providerId;

    @Builder
    public SocialMember(Long idx, String id, AuthProvider provider, String providerId) {
        this.idx = idx;
        this.id = id;
        this.provider = provider;
        this.providerId = providerId;
    }
}
