package com.member.easysignapp.controller.api;

import com.member.easysignapp.dto.ApiResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class CsrfController {
//    private final CsrfTokenRepository csrfTokenRepository;
//
//    public CsrfController(CsrfTokenRepository csrfTokenRepository) {
//        this.csrfTokenRepository = csrfTokenRepository;
//    }
//    @GetMapping("/getcsrf")
//    public CsrfToken getCsrfToken(HttpServletRequest request, HttpServletResponse response) {
//        CsrfToken csrfToken = csrfTokenRepository.generateToken(request);
//        csrfTokenRepository.saveToken(csrfToken, request, response);
//        return csrfToken;
//    }

    @GetMapping("/getcsrf")
    public ApiResponse getCsrfToken(HttpServletRequest servletRequest) {
        return ApiResponse.builder()
                .status("success")
                .csrfToken(((CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName())).getToken())
                .msg("Success message")
                .build();
    }
}