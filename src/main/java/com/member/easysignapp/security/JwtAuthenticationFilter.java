package com.member.easysignapp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.member.easysignapp.dto.ApiResponse;
import com.member.easysignapp.dto.TokenInfo;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    private final MessageSourceAccessor messageSourceAccessor;

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Request Header 에서 JWT 토큰 추출
        String token = resolveToken(request);

        try {
            if (token != null) {
                // 2. 토큰 유효성 검사
                jwtTokenProvider.validateToken(token);
                // 3. 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                // 4. 인증 객체 생성 및 보안 컨텍스트에 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우 refreshToken 체크
            handleExpiredJwtException(request, response, token, e);
            return; // 필터 체인 중단
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid JWT", e);
        } catch (SecurityException e) {
            throw new RuntimeException("Forbidden", e);
        }

        filterChain.doFilter(request, response);
    }

    private void handleExpiredJwtException(HttpServletRequest request, HttpServletResponse response, String token, ExpiredJwtException e) throws IOException {
        // 만료된 토큰이라도 "tokenType"을 추출하여 처리
        Claims claims = e.getClaims();
        String tokenType = claims.get("tokenType", String.class);

        if ("refresh".equals(tokenType)) {
            //refresh 일때는 검증 및 재발행
            boolean isRefreshTokenValid = jwtTokenProvider.isRefreshTokenValid(token, claims.getSubject());

            if (isRefreshTokenValid) {
                // Refresh 토큰으로부터 유저 정보 및 권한 추출
                Authentication authentication = jwtTokenProvider.getAuthentication(token);

                // 새로운 액세스 토큰 발급 및 리턴
                TokenInfo newTokenInfo = jwtTokenProvider.generateToken(authentication);

                // Access Refresh 토큰 생성후 전달
                String createdSuccessMessage = messageSourceAccessor.getMessage("jwt.authFilter.createdToken.success.message");

                ApiResponse apiResponse = ApiResponse.builder()
                        .status("success")
                        .csrfToken(((CsrfToken) request.getAttribute(CsrfToken.class.getName())).getToken())
                        .msg(createdSuccessMessage)
                        .data(new ObjectMapper().writeValueAsString(newTokenInfo))
                        .build();

                response.setContentType("application/json");
                response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse));
            } else {
                // refresh 토큰 정보가 올바르지 않음
                String validationFailMessage = messageSourceAccessor.getMessage("jwt.authFilter.validationToken.fail.message");

                throw new RuntimeException(validationFailMessage);
            }
        } else {
            //access 토큰이 만료 되었을 때 refresh 토큰 요청
            throw new RuntimeException("Please provide a refresh token.");
        }
    }
}