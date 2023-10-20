package com.member.easysignapp.exception;

import com.member.easysignapp.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final CsrfTokenRepository csrfTokenRepository;

    @Autowired
    public GlobalExceptionHandler(CsrfTokenRepository csrfTokenRepository) {
        this.csrfTokenRepository = csrfTokenRepository;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(HttpServletRequest request, Exception ex) {
        CsrfToken csrfToken = csrfTokenRepository.loadToken(request);

        ApiResponse response = ApiResponse.builder()
                .status("fail")
                .csrfToken(csrfToken.getToken())
                .msg(ex.getMessage())
                .build();

        return ResponseEntity.ok(response);
    }
}

