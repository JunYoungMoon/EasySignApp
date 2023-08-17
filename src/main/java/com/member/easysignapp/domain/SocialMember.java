package com.member.easysignapp.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Setter
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String id;
    private String provider;
    private String providerId;

    @Builder
    public SocialMember(Long idx, String id, String provider, String providerId) {
        this.idx = idx;
        this.id = id;
        this.provider = provider;
        this.providerId = providerId;
    }
}
