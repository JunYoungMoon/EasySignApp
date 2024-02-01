package com.member.easysignapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    /*SSR 방식은 현재 사용하지 않습니다.*/
    @GetMapping("/")
    public String sendData(Model model) {
        String message = "Hello, world!!";
        String header = "Header";
        model.addAttribute("message", message); // 모델에 데이터 추가
        model.addAttribute("header", header); // 모델에 데이터 추가
        return "pages/index"; // 데이터가 표시될 HTML 파일 이름 반환
    }
}
