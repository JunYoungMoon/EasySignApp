package com.member.easysignapp.controller.api;

import com.member.easysignapp.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class CsrfController {
    private final MessageSourceAccessor messageSourceAccessor;

    @Autowired
    public CsrfController(MessageSourceAccessor messageSourceAccessor) {
        this.messageSourceAccessor = messageSourceAccessor;
    }
    @GetMapping("/getcsrf")
    public ApiResponse getCsrfToken(HttpServletRequest servletRequest) {

        String successMessage = messageSourceAccessor.getMessage("success.message.key");

        return ApiResponse.builder()
                .status("success")
                .csrfToken(((CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName())).getToken())
                .msg(successMessage)
                .build();
    }
}