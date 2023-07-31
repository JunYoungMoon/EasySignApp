package com.member.easysignapp.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
}
