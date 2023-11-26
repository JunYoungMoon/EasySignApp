package com.member.easysignapp.controller.api;

import com.member.easysignapp.dto.ApiResponse;
import com.member.easysignapp.dto.MemberRequest;
import com.member.easysignapp.service.MemberService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api")
public class MemberController {
    @Value("${upload.profile.directory}")
    private String uploadPath;

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/signup")
    public ApiResponse signUp(HttpServletRequest servletRequest, @RequestBody MemberRequest memberRequest) {
        return ApiResponse.builder()
                .status("success")
                .csrfToken(((CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName())).getToken())
                .msg("Success message")
                .data(memberService.signUp(memberRequest))
                .build();
    }

    @PostMapping("/login")
    public ApiResponse login(HttpServletRequest servletRequest, @RequestBody MemberRequest memberRequest) {
        return ApiResponse.builder()
                .status("success")
                .csrfToken(((CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName())).getToken())
                .msg("Success message")
                .data(memberService.login(memberRequest))
                .build();
    }

    @PostMapping("/check-auth")
    public ApiResponse checkAuth(HttpServletRequest servletRequest, @AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.builder()
                .status("success")
                .csrfToken(((CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName())).getToken())
                .msg("Success message")
                .data(userDetails != null)
                .build();
    }

    @PostMapping("/test")
    public ApiResponse test(HttpServletRequest servletRequest) {
        Map<String, Object> data = new HashMap<>();
        data.put("auth", true);

        return ApiResponse.builder()
                .status("success")
                .csrfToken(((CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName())).getToken())
                .msg("Success message")
                .data(data)
                .build();
    }

    @PostMapping("/user-info")
    public ApiResponse getUserInfo(HttpServletRequest servletRequest) {
        // 현재 사용자의 인증 객체 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 객체에서 사용자 정보 가져오기
        Object principal = authentication.getPrincipal();
        String uuid = ((UserDetails) principal).getUsername();

        return ApiResponse.builder()
                .status("success")
                .csrfToken(((CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName())).getToken())
                .msg("Success message")
                .data(memberService.userInfo(uuid))
                .build();
    }

    @PostMapping("/set-user-info")
    public ApiResponse setUserInfo(
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "nickname", required = false) String nickname,
            @AuthenticationPrincipal UserDetails userDetails) {
        String uuid = userDetails.getUsername();

        try {
            // 파일 업로드 처리
            String uploadedImagePath = null;
            if (profileImage != null && !profileImage.isEmpty()) {
                // 파일 이름을 유니크하게 만들어서 저장
                String fileName = uuid + "_" + profileImage.getOriginalFilename();
                Path filePath = Paths.get(uploadPath, fileName);

                // 파일 복사
                Files.copy(profileImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                uploadedImagePath = uploadPath + "/" + fileName;
            }

            //닉네임, 프로필이미지 업데이트
            memberService.updateMemberInfo(uuid, nickname, uploadedImagePath);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ApiResponse.builder()
                .status("success")
                .msg("User information updated successfully")
                .build();

    }
}
