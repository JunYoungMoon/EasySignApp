package com.mjy.easysignapp.service;

import com.mjy.easysignapp.dto.EmailRequest;

public interface MailService {
    String sendEmail(EmailRequest emailRequest, String title, String content);
}
