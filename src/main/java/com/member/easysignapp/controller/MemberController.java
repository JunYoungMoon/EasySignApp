package com.member.easysignapp.controller;

import com.member.easysignapp.entity.Member;
import com.member.easysignapp.dto.TokenInfo;
import com.member.easysignapp.dto.MemberRequest;
import com.member.easysignapp.service.MemberService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/signup")
    public Member signUp(@RequestBody MemberRequest request) {
        return memberService.signUp(request);
    }

    @PostMapping("/login")
    public TokenInfo login(@RequestBody MemberRequest request) {
        return memberService.login(request);
    }

    @GetMapping("/userinfo")
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

    @PostMapping("/test")
    public String test() {
        return "success";
    }
}
