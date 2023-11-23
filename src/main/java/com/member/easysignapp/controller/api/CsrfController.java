package com.member.easysignapp.controller.api;

import com.member.easysignapp.dto.ApiResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class CsrfController {
    @GetMapping("/getcsrf")
    public ApiResponse getCsrfToken(HttpServletRequest servletRequest) {
        return ApiResponse.builder()
                .status("success")
                .csrfToken(((CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName())).getToken())
                .msg("Success message")
                .build();
    }
}