package com.member.easysignapp.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberInfo {
    private String email;
    private String profileImage;
}