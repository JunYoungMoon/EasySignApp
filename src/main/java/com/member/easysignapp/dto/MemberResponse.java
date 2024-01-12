package com.member.easysignapp.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MemberResponse {
    private String email;
    private String name;
}
