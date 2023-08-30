package com.member.easysignapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String sendData(Model model) {
        String message = "Hello, world!";
        model.addAttribute("message", message); // 모델에 데이터 추가
        return "pages/index"; // 데이터가 표시될 HTML 파일 이름 반환
    }
}
