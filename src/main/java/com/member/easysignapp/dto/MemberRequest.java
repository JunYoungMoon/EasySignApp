package com.member.easysignapp.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
public class MemberRequest extends BaseRequest{
    @NotEmpty(message = "비밀번호는 필수 입력 사항 입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "비밀번호는 최소 8자 이상이어야 하며, 최소 하나의 문자와 하나의 숫자를 포함해야 합니다.")
    private String password;
    @NotEmpty(message = "이름은 필수 입력 사항 입니다.")
    private String name;
    private List<String> roles;
}
