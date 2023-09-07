package com.member.easysignapp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.member.easysignapp.dto.TokenInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
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

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

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
            // 토큰이 만료된 경우의 예외 처리
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
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                    response.getWriter().write("Validation failed with the corresponding refresh token.");
                }
            } else {
                //access일 경우에는 refresh 토큰 요청
                response.setStatus(HttpServletResponse.SC_OK); // 200 OK
                response.getWriter().write("Refresh token required");
            }
            return; // 필터 체인 중단
        } catch (UnsupportedJwtException | MalformedJwtException e) {
            // 지원하지 않는 JWT 또는 잘못된 형식의 JWT에 대한 예외 처리
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
            response.getWriter().write("Invalid JWT");
            return; // 필터 체인 중단
        } catch (IllegalArgumentException e) {
            // 잘못된 JWT claim 값에 대한 예외 처리
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
            response.getWriter().write("Invalid JWT claims");
            return; // 필터 체인 중단
        } catch (SecurityException e) {
            // 보안 문제에 대한 예외 처리
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden
            response.getWriter().write("Forbidden");
            return; // 필터 체인 중단
        }

        filterChain.doFilter(request, response);
    }

    // Request Header 에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
