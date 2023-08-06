package com.member.easysignapp.controller;

import com.member.easysignapp.domain.Member;
import com.member.easysignapp.domain.TokenInfo;
import com.member.easysignapp.dto.MemberRequest;
import com.member.easysignapp.service.MemberService;
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
    public Member signUp(@RequestBody MemberRequest request) {
        return memberService.signUp(request.getUsername(), request.getEmail(), request.getPassword(), request.getRoles());
    }

    @PostMapping("/login")
    public TokenInfo login(@RequestBody MemberRequest request) {

        String email = request.getEmail();
        String password = request.getPassword();

        return memberService.login(email, password);
    }
}
