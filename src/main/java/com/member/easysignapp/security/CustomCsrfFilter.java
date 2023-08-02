package com.member.easysignapp.security;

import org.springframework.core.Ordered;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomCsrfFilter extends OncePerRequestFilter{

    private static final String MOBILE_USER_AGENT = "Mobile";

    private final CsrfTokenRepository csrfTokenRepository;

    public CustomCsrfFilter(CsrfTokenRepository csrfTokenRepository) {
        this.csrfTokenRepository = csrfTokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isMobileRequest(request)) {
            // 모바일일때 CSRF 검증 체크 스킵
            filterChain.doFilter(request, response);
        } else {
            // 웹일때 CSRF 검증
            CsrfToken csrfToken = csrfTokenRepository.loadToken(request);
            if (csrfToken != null) {
                String actualToken = request.getHeader(csrfToken.getHeaderName());
                if (actualToken == null || !actualToken.equals(csrfToken.getToken())) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF Token");
                    return;
                }
            }
            filterChain.doFilter(request, response);
        }
    }

    private boolean isMobileRequest(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null && userAgent.contains(MOBILE_USER_AGENT);
    }
}
