package com.backoffice.upjuyanolja.domain.member.service;

import com.backoffice.upjuyanolja.domain.member.exception.SendEmailErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender emailSender;

    public void sendMail(
        String toEmail,
        String title,
        String text) {

        SimpleMailMessage emailForm = createEmailForm(toEmail, title, text);
        try {
            emailSender.send(emailForm);
        } catch (MailException e) {
            log.error(e.getMessage());
            throw new SendEmailErrorException();
        }

    }

    // 발신할 이메일 데이터 세팅
    private SimpleMailMessage createEmailForm(
        String toEmail,
        String title,
        String text) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject(title);
        message.setText(text);

        return message;
    }

}
