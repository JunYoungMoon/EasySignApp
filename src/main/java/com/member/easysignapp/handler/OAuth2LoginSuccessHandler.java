package com.member.easysignapp.handler;

import com.member.easysignapp.dto.TokenInfo;
import com.member.easysignapp.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${client.app.url}")
    private String clientUrl;

//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//        log.info("OAuth2 Login 성공");
//        //JWT 생성
//        TokenInfo newTokenInfo = jwtTokenProvider.generateToken(authentication);
//
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String jsonResponse = objectMapper.writeValueAsString(newTokenInfo);
//        response.getWriter().write(jsonResponse);
//    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("OAuth2 Login 성공");
        // JWT 생성
        TokenInfo newTokenInfo = jwtTokenProvider.generateToken(authentication);

        // JWT를 GET 파라미터로 전달할 URL 생성
        UriComponentsBuilder redirectUrlBuilder = UriComponentsBuilder.fromUriString(clientUrl + "/auth/callback");
        redirectUrlBuilder.queryParam("accessToken", newTokenInfo.getAccessToken());
        redirectUrlBuilder.queryParam("refreshToken", newTokenInfo.getRefreshToken());
        redirectUrlBuilder.queryParam("csrfToken", request.getAttribute("myCsrfToken"));
        String redirectUrl = redirectUrlBuilder.toUriString();

        // 클라이언트로 리다이렉트
        response.sendRedirect(redirectUrl);
    }
}
