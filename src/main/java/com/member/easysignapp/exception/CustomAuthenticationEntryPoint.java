package com.member.easysignapp.exception;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final String loginPageUrl;

    public CustomAuthenticationEntryPoint(String loginPageUrl) {
        this.loginPageUrl = loginPageUrl;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // 인증이 실패하거나 권한이 없는 경우, 클라이언트 로그인 페이지로 리다이렉트
        response.sendRedirect(loginPageUrl);
    }
}
