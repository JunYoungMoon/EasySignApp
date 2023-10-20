package com.member.easysignapp.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // 빈 값을 포함하지 않도록 설정
public class ApiResponse {
    private String status;
    private String csrfToken;
    private String msg;
    private Object data;
}