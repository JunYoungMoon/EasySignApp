package com.member.easysignapp.controller.api;

import com.member.easysignapp.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/csrf")
public class CsrfController {
    private final MessageSourceAccessor messageSourceAccessor;

    @Autowired
    public CsrfController(MessageSourceAccessor messageSourceAccessor) {
        this.messageSourceAccessor = messageSourceAccessor;
    }

    @GetMapping
    public ApiResponse getCsrfToken(HttpServletRequest servletRequest) {
        CsrfToken csrfToken = (CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName());

        String successMessage = messageSourceAccessor.getMessage("csrf.success.message", new Object[]{csrfToken.getToken()});

        return ApiResponse.builder()
                .status("success")
                .csrfToken(csrfToken.getToken())
                .msg(successMessage)
                .build();
    }
}