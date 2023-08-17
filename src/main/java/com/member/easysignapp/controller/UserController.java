package com.member.easysignapp.controller;

import com.member.easysignapp.domain.User;
import com.member.easysignapp.domain.TokenInfo;
import com.member.easysignapp.dto.MemberRequest;
import com.member.easysignapp.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public User signUp(@RequestBody MemberRequest request) {
        return userService.signUp(request.getId(), request.getEmail(), request.getPassword(), request.getRoles());
    }

    @PostMapping("/login")
    public TokenInfo login(@RequestBody MemberRequest request) {

        String id = request.getId();
        String password = request.getPassword();

        return userService.login(id, password);
    }

    @PostMapping("/test")
    public String test() {
        return "success";
    }
}
