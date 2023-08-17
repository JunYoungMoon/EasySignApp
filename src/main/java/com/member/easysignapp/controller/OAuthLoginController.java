package com.member.easysignapp.controller;

import com.member.easysignapp.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/login/oauth2", produces = "application/json")
public class OAuthLoginController {
    private final CustomOAuth2UserService customOAuth2UserService;

    @GetMapping("/code/{registrationId}")
    public void googleLogin(@RequestParam String code, @PathVariable String registrationId) {
//        memberOauth2UserService.socialLogin(code, registrationId);
    }
}
