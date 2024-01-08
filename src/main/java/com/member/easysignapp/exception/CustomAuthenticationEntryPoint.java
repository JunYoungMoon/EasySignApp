package com.member.easysignapp.exception;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // 인증이 실패하거나 권한이 없는 경우, 클라이언트에 메시지 전달
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Authentication failed or insufficient permissions.");

        // JSON 형식의 응답을 전달 필요시 다른걸로 변경 가능
        response.setContentType("application/json");
    }
}
