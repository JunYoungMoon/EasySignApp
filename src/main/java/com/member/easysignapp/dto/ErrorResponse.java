package com.member.easysignapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
}
