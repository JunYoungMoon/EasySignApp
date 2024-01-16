package com.member.easysignapp.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class BaseRequest {
    @NotEmpty(message = "이메일은 필수 입력 사항 입니다.")
    @Email(message = "올바른 이메일 형식 이어야 합니다.")
    private String email;
}
