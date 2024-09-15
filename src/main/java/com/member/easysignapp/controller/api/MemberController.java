package com.member.easysignapp.controller.api;

import com.member.easysignapp.dto.*;
import com.member.easysignapp.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.member.easysignapp.util.FileUtil.saveProfileImage;

@RestController
@RequestMapping("/api/users")
public class MemberController {
    @Value("${upload.profile.directory}")
    private String uploadPath;

    private final MemberService memberService;
    private final MessageSourceAccessor messageSourceAccessor;

    @Autowired
    public MemberController(MemberService memberService, MessageSourceAccessor messageSourceAccessor) {
        this.memberService = memberService;
        this.messageSourceAccessor = messageSourceAccessor;
    }

    @Operation(summary = "회원 등록", description = "새로운 회원을 등록합니다.")
    @PostMapping
    public ApiResponse registerUser(HttpServletRequest servletRequest, @RequestBody @Valid MemberRequest memberRequest) {
        String successMessage = messageSourceAccessor.getMessage("member.registerUser.success.message");

        return ApiResponse.builder()
                .status("success")
                .csrfToken(((CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName())).getToken())
                .msg(successMessage)
                .data(memberService.registerUser(memberRequest))
                .build();
    }

    @Operation(summary = "회원 로그인", description = "회원 로그인을 처리합니다.")
    @PostMapping("/login")
    public ApiResponse login(HttpServletRequest servletRequest, @RequestBody MemberRequest memberRequest) {
        String successMessage = messageSourceAccessor.getMessage("member.login.success.message");

        return ApiResponse.builder()
                .status("success")
                .csrfToken(((CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName())).getToken())
                .msg(successMessage)
                .data(memberService.login(memberRequest))
                .build();
    }

    @Operation(summary = "인증 상태 확인", description = "현재 로그인된 사용자의 인증 상태를 확인합니다.")
    @PostMapping("/check-auth")
    public ApiResponse checkAuth(HttpServletRequest servletRequest, @AuthenticationPrincipal UserDetails userDetails) {
        String successMessage = messageSourceAccessor.getMessage("member.checkAuth.success.message");

        return ApiResponse.builder()
                .status("success")
                .csrfToken(((CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName())).getToken())
                .msg(successMessage)
                .data(userDetails != null)
                .build();
    }

    @Operation(summary = "회원 정보 조회", description = "현재 로그인된 사용자의 정보를 반환합니다.")
    @GetMapping("/me")
    public ApiResponse getUserInfo(HttpServletRequest servletRequest) {
        // 현재 사용자의 인증 객체 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 객체에서 사용자 정보 가져오기
        Object principal = authentication.getPrincipal();
        String uuid = ((UserDetails) principal).getUsername();

        String successMessage = messageSourceAccessor.getMessage("member.getUserInfo.success.message");

        return ApiResponse.builder()
                .status("success")
                .csrfToken(((CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName())).getToken())
                .msg(successMessage)
                .data(memberService.userInfo(uuid))
                .build();
    }

    @Operation(summary = "회원 정보 수정", description = "현재 로그인된 사용자의 정보를 수정합니다.")
    @PutMapping("/me")
    public ApiResponse setUserInfo(
            HttpServletRequest servletRequest,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "nickname", required = false) String nickname,
            @AuthenticationPrincipal UserDetails userDetails) {
        String uuid = userDetails.getUsername();
        String newProfileImage;

        try {
            newProfileImage = saveProfileImage(profileImage, uuid, uploadPath);
            memberService.updateMemberInfo(uuid, nickname, newProfileImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> responseData = new HashMap<>();

        if (nickname != null) {
            responseData.put("newNickName", nickname);
        }

        if (newProfileImage != null) {
            responseData.put("newProfileImage", "/profile/" + newProfileImage);
        }

        String successMessage = messageSourceAccessor.getMessage("member.setUserInfo.success.message");

        return ApiResponse.builder()
                .status("success")
                .csrfToken(((CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName())).getToken())
                .msg(successMessage)
                .data(responseData)
                .build();
    }

    @Operation(summary = "테스트 엔드포인트", description = "테스트용 엔드포인트입니다.")
    @PostMapping("/test")
    public ApiResponse test(HttpServletRequest servletRequest) {
        String successMessage = messageSourceAccessor.getMessage("member.test.message");

        Map<String, Object> data = new HashMap<>();
        data.put("auth", true);

        return ApiResponse.builder()
                .status("success")
                .csrfToken(((CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName())).getToken())
                .msg(successMessage)
                .data(data)
                .build();
    }
}
