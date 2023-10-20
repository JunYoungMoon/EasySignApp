package com.member.easysignapp.controller.api;

import com.member.easysignapp.common.ApiResponse;
import com.member.easysignapp.dto.MemberInfo;
import com.member.easysignapp.dto.MemberRequest;
import com.member.easysignapp.service.MemberService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MemberController {
    private final MemberService memberService;
    private final CsrfTokenRepository csrfTokenRepository;
    public MemberController(MemberService memberService, CsrfTokenRepository csrfTokenRepository) {
        this.memberService = memberService;
        this.csrfTokenRepository = csrfTokenRepository;
    }

    @PostMapping("/signup")
    public ApiResponse signUp(HttpServletRequest servletRequest, HttpServletResponse servletResponse, @RequestBody MemberRequest request) {
        CsrfToken csrfToken = csrfTokenRepository.generateToken(servletRequest);
        csrfTokenRepository.saveToken(csrfToken, servletRequest, servletResponse);

        return ApiResponse.builder()
                .status("success")
                .csrfToken(csrfToken.getToken())
                .msg("Success message")
                .data(memberService.signUp(request))
                .build();
    }

    @PostMapping("/login")
    public ApiResponse login(@RequestBody MemberRequest request, CsrfToken csrfToken) {
        return ApiResponse.builder()
                .status("success")
                .csrfToken(csrfToken.getToken())
                .msg("Success message")
                .data(memberService.login(request))
                .build();
    }

    @PostMapping("/check-auth")
    public boolean checkAuth(@AuthenticationPrincipal UserDetails userDetails) {
        return userDetails != null;
    }

    @PostMapping("/test")
    public ApiResponse test(CsrfToken csrfToken) {
        Map<String, Object> data = new HashMap<>();
        data.put("auth", true);

        return ApiResponse.builder()
                .status("success")
                .csrfToken(csrfToken.getToken())
                .msg("Success message")
                .data(data)
                .build();
    }

    @PostMapping("/user-info")
    public MemberInfo getUserInfo() {
        // 현재 사용자의 인증 객체 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 객체에서 사용자 정보 가져오기
        Object principal = authentication.getPrincipal();
        String uuid = ((UserDetails) principal).getUsername();

        return memberService.userInfo(uuid);
    }
}
