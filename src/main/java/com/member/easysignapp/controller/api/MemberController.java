package com.member.easysignapp.controller.api;

import com.member.easysignapp.dto.MemberResponse;
import com.member.easysignapp.dto.TokenInfo;
import com.member.easysignapp.dto.MemberRequest;
import com.member.easysignapp.service.MemberService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
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

    @PostMapping("/user-info")
    public String getUserInfo() {
        // 현재 사용자의 인증 객체 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 객체에서 사용자 정보 가져오기
        Object principal = authentication.getPrincipal();
        String username = ((UserDetails) principal).getUsername();

        // 사용자의 권한 정보 가져오기
        // List<GrantedAuthority> authorities = (List<GrantedAuthority>) authentication.getAuthorities();

        return "Username: " + username;
    }
}
