package com.member.easysignapp.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MemberRequest {
    private String username;
    private String email;
    private String password;
}
