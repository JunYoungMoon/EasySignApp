package com.member.easysignapp.exception;

import com.member.easysignapp.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(HttpServletRequest servletRequest, Exception ex) {

        ApiResponse response = ApiResponse.builder()
                .status("fail")
                .csrfToken((String) servletRequest.getAttribute("myCsrfToken"))
                .msg(ex.getMessage())
                .build();

        return ResponseEntity.ok(response);
    }
}

