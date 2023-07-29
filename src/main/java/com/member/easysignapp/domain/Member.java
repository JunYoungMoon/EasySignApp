package com.member.easysignapp.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Member {
    private Long id;
    private String username;
    private String email;
    private String password;
}
