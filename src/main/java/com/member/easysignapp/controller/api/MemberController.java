package com.member.easysignapp.controller.api;

import com.member.easysignapp.annotation.RateLimit;
import com.member.easysignapp.dto.*;
import com.member.easysignapp.service.MemberService;
import com.member.easysignapp.util.CheckSumUtil;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.member.easysignapp.util.FileUtil.saveProfileImage;

@RestController
@RequestMapping("/api")
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
    @RateLimit(key = "sendMail", limit = 2, period = 60000)
    public ApiResponse sendMail(HttpServletRequest servletRequest, @RequestBody @Valid EmailRequest emailRequest) {
        String email = emailRequest.getEmail();
        String checksumFromClient = emailRequest.getCheckSum();

        // 클라이언트가 전송한 데이터를 사용하여 체크섬 재생성
        String calculatedCheckSum = CheckSumUtil.generateCheckSum(email);

        // 클라이언트가 전송한 체크섬과 재생성한 체크섬 비교
        if (calculatedCheckSum.equals(checksumFromClient)) {
            memberService.sendCodeToEmail(emailRequest);

            String successMessage = messageSourceAccessor.getMessage("member.sendMail.success.message");

            return ApiResponse.builder()
                    .status("success")
                    .csrfToken(((CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName())).getToken())
                    .msg(successMessage)
                    .build();
        } else {
            // 체크섬이 일치하지 않으면 오류 응답 반환
            return ApiResponse.builder()
                    .status("fail")
                    .msg("Checksum verification failed.")
                    .build();
        }
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

//    private String generateChecksum(String data) {
//        // 데이터에 대한 체크섬 생성 로직
//        // 여기서는 단순히 SHA-256을 사용한 예시입니다. 실제로는 더 강력한 알고리즘을 사용해야 합니다.
//        return DigestUtils.sha256Hex(data);
//    }
}
