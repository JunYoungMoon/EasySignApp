package com.member.easysignapp.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

@Getter
@Setter
public class EmailRequest extends BaseRequest {

    private String checkSum;
}
