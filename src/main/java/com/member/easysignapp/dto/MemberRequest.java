package com.member.easysignapp.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class MemberRequest {
    private String id;
    private String email;
    private String password;
    private String name;
    private List<String> roles;
}
