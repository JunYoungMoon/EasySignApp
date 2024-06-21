package com.member.easysignapp.controller.api;

import com.member.easysignapp.annotation.RateLimit;
import com.member.easysignapp.dto.ApiResponse;
import com.member.easysignapp.dto.EmailRequest;
import com.member.easysignapp.dto.EmailVerificationRequest;
import com.member.easysignapp.service.MemberService;
import com.member.easysignapp.util.CheckSumUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/mail")
public class MailController {
    private final MemberService memberService;
    private final MessageSourceAccessor messageSourceAccessor;

    @Autowired
    public MailController(MemberService memberService, MessageSourceAccessor messageSourceAccessor) {
        this.memberService = memberService;
        this.messageSourceAccessor = messageSourceAccessor;
    }

    @PostMapping("/send")
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

    @PostMapping("/verify")
    public ApiResponse verificationEmail(HttpServletRequest servletRequest, @RequestBody @Valid EmailVerificationRequest emailVerificationRequest) {
        memberService.verifiedCode(emailVerificationRequest);

        String successMessage = messageSourceAccessor.getMessage("member.verificationEmail.success.message");

        return ApiResponse.builder()
                .status("success")
                .csrfToken(((CsrfToken) servletRequest.getAttribute(CsrfToken.class.getName())).getToken())
                .msg(successMessage)
                .build();
    }
}
