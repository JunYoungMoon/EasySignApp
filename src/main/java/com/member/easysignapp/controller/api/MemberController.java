package com.member.easysignapp.controller.api;

import com.member.easysignapp.dto.ApiResponse;
import com.member.easysignapp.dto.MemberRequest;
import com.member.easysignapp.dto.UserInfoRequest;
import com.member.easysignapp.service.MemberService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/signup")
    public ApiResponse signUp(HttpServletRequest servletRequest, @RequestBody MemberRequest memberRequest) {
        return ApiResponse.builder()
                .status("success")
                .csrfToken(((CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName())).getToken())
                .msg("Success message")
                .data(memberService.signUp(memberRequest))
                .build();
    }

    @PostMapping("/login")
    public ApiResponse login(HttpServletRequest servletRequest, @RequestBody MemberRequest memberRequest) {
        return ApiResponse.builder()
                .status("success")
                .csrfToken(((CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName())).getToken())
                .msg("Success message")
                .data(memberService.login(memberRequest))
                .build();
    }

    @PostMapping("/check-auth")
    public ApiResponse checkAuth(HttpServletRequest servletRequest, @AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.builder()
                .status("success")
                .csrfToken(((CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName())).getToken())
                .msg("Success message")
                .data(userDetails != null)
                .build();
    }

    @PostMapping("/test")
    public ApiResponse test(HttpServletRequest servletRequest) {
        Map<String, Object> data = new HashMap<>();
        data.put("auth", true);

        return ApiResponse.builder()
                .status("success")
                .csrfToken(((CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName())).getToken())
                .msg("Success message")
                .data(data)
                .build();
    }

    @PostMapping("/user-info")
    public ApiResponse getUserInfo(HttpServletRequest servletRequest) {
        // 현재 사용자의 인증 객체 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 객체에서 사용자 정보 가져오기
        Object principal = authentication.getPrincipal();
        String uuid = ((UserDetails) principal).getUsername();

        return ApiResponse.builder()
                .status("success")
                .csrfToken(((CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName())).getToken())
                .msg("Success message")
                .data(memberService.userInfo(uuid))
                .build();
    }

    @PostMapping("/set-user-info")
    public ApiResponse setUserInfo(@RequestBody UserInfoRequest userInfoRequest) {
        try {
            // Retrieve the current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();
            String uuid = ((UserDetails) principal).getUsername();

            // Update user information using your service
//            memberService.setUserInfo(uuid, userInfoRequest);

            return ApiResponse.builder()
                    .status("success")
                    .msg("User information updated successfully")
                    .build();
        } catch (Exception e) {
            return ApiResponse.builder()
                    .status("error")
                    .msg("Failed to update user information")
                    .build();
        }
    }

}
