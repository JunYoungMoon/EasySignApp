package com.member.easysignapp.controller.api;

import com.member.easysignapp.annotation.RateLimit;
import com.member.easysignapp.dto.ApiResponse;
import com.member.easysignapp.dto.EmailRequest;
import com.member.easysignapp.dto.EmailVerificationRequest;
import com.member.easysignapp.service.MemberService;
import com.member.easysignapp.util.CheckSumUtil;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "메일 전송", description = "메일을 전송합니다.")
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

    @Operation(summary = "메일 인증 코드 검증", description = "발송한 메일의 인증코드를 검증합니다.")
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
