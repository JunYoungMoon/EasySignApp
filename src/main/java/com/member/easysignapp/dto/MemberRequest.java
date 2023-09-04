package com.member.easysignapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MemberRequest {
    private String id;
    private String email;
    private String password;
    private String name;
    private List<String> roles;
}
