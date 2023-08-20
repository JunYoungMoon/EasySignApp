package com.member.easysignapp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
class OAuth2LoginController {

    @GetMapping("/login/oauth2/code/google")
    public String handleGoogleLogin(Authentication authentication) {
        // Google 로그인이 완료되고 승인된 경우 호출되는 핸들러
        // Authentication 객체를 통해 로그인된 사용자 정보에 접근할 수 있음

        // 예를 들어, 로그인된 사용자의 정보 출력
        System.out.println("Logged in user: " + authentication.getName());

        // 추가적인 처리나 리다이렉션 등을 수행하고 반환
        return "redirect:/profile"; // 프로필 페이지로 리다이렉션
    }

    // 프로필 페이지 컨트롤러 등을 여기에 추가할 수 있음
}