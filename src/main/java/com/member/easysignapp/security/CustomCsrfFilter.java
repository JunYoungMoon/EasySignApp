package com.member.easysignapp.security;

import org.springframework.security.web.csrf.*;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomCsrfFilter extends OncePerRequestFilter {

    private static final String MOBILE_USER_AGENT = "Mobile";

    private final CsrfTokenRepository csrfTokenRepository;

    public CustomCsrfFilter(CsrfTokenRepository csrfTokenRepository) {
        this.csrfTokenRepository = csrfTokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isMobileRequest(request) || request.getRequestURI().equals("/getcsrf") || isOAuthLoginRequest(request)) {
            // 모바일일때와 getcsrf일때 CSRF 검증 체크 스킵
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
            } else {
                csrfToken = this.csrfTokenRepository.generateToken(request);
                this.csrfTokenRepository.saveToken(csrfToken, request, response);
            }

            filterChain.doFilter(request, response);
        }
    }

    private boolean isMobileRequest(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null && userAgent.contains(MOBILE_USER_AGENT);
    }

    private boolean isOAuthLoginRequest(HttpServletRequest request) {
        String[] segments = request.getRequestURI().split("/"); // URL 세그먼트를 분리
        String provider = segments[segments.length - 1]; // 마지막 세그먼트

        String oauthLoginPath;

        // 공급자별로 로그인 요청 URL 패턴을 설정
        switch (provider) {
            case "google":
                oauthLoginPath = "/login/oauth2/code/google"; // 구글 OAuth 로그인 요청 URL 패턴
                break;
            case "facebook":
                oauthLoginPath = "/login/oauth2/code/facebook"; // 페이스북 OAuth 로그인 요청 URL 패턴
                break;
            // 다른 공급자의 경우도 필요한대로 추가
            default:
                return false; // 처리할 공급자가 없으면 false 반환
        }

        return request.getRequestURI().equals(oauthLoginPath);
    }
}
