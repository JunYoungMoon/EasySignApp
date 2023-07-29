package com.member.easysignapp.controller;

import com.member.easysignapp.domain.Member;
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
        // 클라이언트로부터 전달된 요청을 바탕으로 MemberService를 호출합니다.
        return memberService.signUp(request.getUsername(), request.getEmail(), request.getPassword());
    }
}
