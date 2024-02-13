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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.member.easysignapp.util.FileUtil.sanitizeFileName;

@RestController
@RequestMapping("/api")
public class MemberController {
    @Value("${upload.profile.directory}")
    private String uploadPath;
    @Value("${server.app.url}")
    private String serverUrl;

    private final MemberService memberService;
    private final MessageSourceAccessor messageSourceAccessor;

    @Autowired
    public MemberController(MemberService memberService, MessageSourceAccessor messageSourceAccessor) {
        this.memberService = memberService;
        this.messageSourceAccessor = messageSourceAccessor;
    }

    @PostMapping("/signup")
    public ApiResponse signUp(HttpServletRequest servletRequest, @RequestBody @Valid MemberRequest memberRequest) {
        String successMessage = messageSourceAccessor.getMessage("member.signUp.success.message");

        return ApiResponse.builder()
                .status("success")
                .csrfToken(((CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName())).getToken())
                .msg(successMessage)
                .data(memberService.signUp(memberRequest))
                .build();
    }

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

    @PostMapping("/send-email-code")
    public ApiResponse sendMail(HttpServletRequest servletRequest, @RequestBody @Valid EmailRequest emailRequest) {
        memberService.sendCodeToEmail(emailRequest);

        String successMessage = messageSourceAccessor.getMessage("member.sendMail.success.message");

        return ApiResponse.builder()
                .status("success")
                .csrfToken(((CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName())).getToken())
                .msg(successMessage)
                .build();
    }

    @PostMapping("/email-verification")
    public ApiResponse verificationEmail(HttpServletRequest servletRequest, @RequestBody @Valid EmailVerificationRequest emailVerificationRequest) {
        memberService.verifiedCode(emailVerificationRequest);

        String successMessage = messageSourceAccessor.getMessage("member.verificationEmail.success.message");

        return ApiResponse.builder()
                .status("success")
                .csrfToken(((CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName())).getToken())
                .msg(successMessage)
                .build();
    }

    @PostMapping("/user-info")
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

    @PostMapping("/set-user-info")
    public ApiResponse setUserInfo(
            HttpServletRequest servletRequest,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "nickname", required = false) String nickname,
            @AuthenticationPrincipal UserDetails userDetails) {
        String uuid = userDetails.getUsername();
        String uploadedImagePath = null;
        String uploadedImageName = null;

        try {
            // 파일 업로드 처리
            if (profileImage != null && !profileImage.isEmpty()) {
                // 파일 이름을 유니크하게 만들어서 저장
                String originalFileName = profileImage.getOriginalFilename();

                // 파일 이름이 null이 아닌 경우에만 정리 및 저장을 수행
                if (originalFileName != null && !originalFileName.isEmpty()) {
                    String sanitizedFileName = sanitizeFileName(originalFileName);
                    uploadedImageName = uuid + "_" + sanitizedFileName;

                    Path uploadFolderPath = Paths.get(uploadPath);

                    // 폴더가 없는 경우 생성
                    if (!Files.exists(uploadFolderPath)) {
                        Files.createDirectories(uploadFolderPath);
                    }

                    Path filePath = Paths.get(uploadPath, uploadedImageName);

                    // try-with-resources를 사용하여 InputStream 자동으로 닫기
                    try (InputStream inputStream = profileImage.getInputStream()) {
                        // 파일 복사
                        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                        uploadedImagePath = serverUrl + uploadPath + "/" + uploadedImageName;
                    }
                }
            }

            // 닉네임, 프로필이미지 업데이트
            memberService.updateMemberInfo(uuid, nickname, uploadedImagePath);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("uploadedImageName", uploadedImageName);

        String successMessage = messageSourceAccessor.getMessage("member.setUserInfo.success.message");

        return ApiResponse.builder()
                .status("success")
                .csrfToken(((CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName())).getToken())
                .msg(successMessage)
                .data(responseData)
                .build();
    }

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
