package com.member.easysignapp.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    @Column(nullable = false)
    private String id;
    @Column(nullable = false)
    private String email;
    private String password;

    @Builder
    public Member(Long idx, String id, String email, String password, List<String> roles) {
        this.idx = idx;
        this.id = id;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();
}
