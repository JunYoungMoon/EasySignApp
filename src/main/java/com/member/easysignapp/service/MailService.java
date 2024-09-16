package com.member.easysignapp.service;

import com.member.easysignapp.dto.EmailRequest;

public interface MailService {
    String sendEmail(EmailRequest emailRequest, String title, String content);
}
