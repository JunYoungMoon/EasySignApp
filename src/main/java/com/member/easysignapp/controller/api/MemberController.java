package com.member.easysignapp.controller.api;

import com.member.easysignapp.dto.MemberResponse;
import com.member.easysignapp.dto.TokenInfo;
import com.member.easysignapp.dto.MemberRequest;
import com.member.easysignapp.service.MemberService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        if (userDetails != null) {
            return true;
        } else {
            return false;
        }
    }
}
