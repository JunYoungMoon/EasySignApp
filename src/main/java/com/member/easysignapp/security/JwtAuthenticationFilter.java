package com.member.easysignapp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.member.easysignapp.dto.TokenInfo;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
            handleExpiredJwtException(response, token, e);
            return; // 필터 체인 중단
        } catch (JwtException | IllegalArgumentException e) {
            handleHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT");
            return;
        } catch (SecurityException e) {
            handleHttpResponse(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void handleExpiredJwtException(HttpServletResponse response, String token, ExpiredJwtException e) throws IOException {
        // 만료된 토큰이라도 "tokenType"을 추출하여 처리
        Claims claims = e.getClaims();
        String tokenType = claims.get("tokenType", String.class);

        if ("refresh".equals(tokenType)) {
            //refresh 일때는 검증 및 재발행
            boolean isRefreshTokenValid = jwtTokenProvider.isRefreshTokenValid(token);

            if (isRefreshTokenValid) {
                // Refresh 토큰으로부터 유저 정보 및 권한 추출
                Authentication authentication = jwtTokenProvider.getAuthentication(token);

                // 새로운 액세스 토큰 발급 및 리턴
                TokenInfo newTokenInfo = jwtTokenProvider.generateToken(authentication);

                // 기존 Refresh 토큰 제거
                jwtTokenProvider.deleteRefreshToken(token);

                // 생성한 액세스 토큰을 응답으로 반환
                response.setContentType("application/json");
                response.getWriter().write(new ObjectMapper().writeValueAsString(newTokenInfo)); // JSON 형식으로 변환하여 응답
                response.setStatus(HttpServletResponse.SC_OK); // 200 OK
            } else {
                // refreshToken이 올바르지 않을때 (401 Unauthorized)
                handleHttpResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Validation failed with the corresponding refresh token.");
            }
        } else {
            //access일 경우에는 refresh 토큰 요청 (200 OK)
            boolean refreshTokenRequired = true;
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("refreshTokenRequired", refreshTokenRequired);

            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(responseData));
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    private void handleHttpResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.getWriter().write(message);
    }
}