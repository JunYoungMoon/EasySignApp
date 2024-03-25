package com.member.easysignapp.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class EmailRequest extends BaseRequest {

    private String checkSum;
}
