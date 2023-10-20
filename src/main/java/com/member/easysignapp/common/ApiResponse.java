package com.member.easysignapp.common;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ApiResponse {
    private String status;
    private String msg;
    private Object data;
}