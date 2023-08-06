package com.member.easysignapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MemberRequest {
    private String username;
    private String email;
    private String password;
    private List<String> roles;
}
