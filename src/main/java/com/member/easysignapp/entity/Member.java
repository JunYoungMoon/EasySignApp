package com.member.easysignapp.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "member",
        indexes = @Index(name = "idx_uuid", columnList = "uuid")
)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    @Column(nullable = false)
    private String id;
    @Column(nullable = false)
    private String uuid;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String name;
    private String password;
    private String profile_image;

    @Builder
    public Member(Long idx, String id, String uuid, String email, String name, String password, List<String> roles, String profile_image) {
        this.idx = idx;
        this.id = id;
        this.uuid = uuid;
        this.email = email;
        this.name = name;
        this.password = password;
        this.roles = roles;
        this.profile_image = profile_image;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();
}
