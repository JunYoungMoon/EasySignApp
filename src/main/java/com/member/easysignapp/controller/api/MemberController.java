package com.member.easysignapp.controller.api;

import com.member.easysignapp.common.ApiResponse;
import com.member.easysignapp.dto.MemberInfo;
import com.member.easysignapp.dto.MemberResponse;
import com.member.easysignapp.dto.TokenInfo;
import com.member.easysignapp.dto.MemberRequest;
import com.member.easysignapp.entity.SocialMember;
import com.member.easysignapp.service.MemberService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
    public MemberResponse signUp(@RequestBody MemberRequest request) {
        return memberService.signUp(request);
    }

    @PostMapping("/login")
    public TokenInfo login(@RequestBody MemberRequest request) {
        return memberService.login(request);
    }

    @PostMapping("/check-auth")
    public boolean checkAuth(@AuthenticationPrincipal UserDetails userDetails) {
        return userDetails != null;
    }

    @PostMapping("/test")
    public ApiResponse test() {
        Map<String, Object> data = new HashMap<>();
        data.put("auth", true);

        return ApiResponse.builder()
                .status("success")
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
