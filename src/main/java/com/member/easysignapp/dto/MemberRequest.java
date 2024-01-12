package com.member.easysignapp.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
public class MemberRequest {
    @NotEmpty(message = "이메일은 필수 입력입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String email;
    private String password;
    private String name;
    private List<String> roles;
}
