package com.member.easysignapp.domain;

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
    private String provider;
    @Column(nullable = false)
    private String providerId;

    @Builder
    public SocialMember(Long idx, String id, String provider, String providerId) {
        this.idx = idx;
        this.id = id;
        this.provider = provider;
        this.providerId = providerId;
    }
}
