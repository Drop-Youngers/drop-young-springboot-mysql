package com.java.main.springstarter.v1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    private final SimpleMailMessage message = new SimpleMailMessage();

    public void sendResetPasswordMail(String toEmail, String names, String activationCodes) {
        message.setFrom("premugisha64@gmail.com");
        message.setTo(toEmail);
        message.setText("Dear " + names + "!\n" +
                "\n" +
                "You've requested to reset password to spring-starter, " +
                "your password reset code is " + activationCodes + ". \n" +
                "\n" +
                "This code expires in 5 minutes.\n" +
                "\n" +
                "If you have any questions, send us an email divin@support.com.\n" +
                "\n" +
                "We’re glad you’re here!\n" +
                "\n");
        message.setSubject("SPRING-STARTER PASSWORD RESET CODE");
        mailSender.send(message);
    }

    public void sendVerificationMail(String toEmail, String names, String verificationCode) {
        message.setFrom("premugisha64@gmail.com");
        message.setTo(toEmail);
        message.setText("Dear " + names + "!\n" +
                "\n" +
                "Here is your account verification code, " +
                "your account verification code is " + verificationCode + ". \n" +
                "\n" +
                "This code expires in 1 hour.\n" +
                "\n" +
                "If you have any questions, send us an email divin@support.com.\n" +
                "\n" +
                "We’re glad you’re here!\n" +
                "\n");
        message.setSubject("SPRING-STARTER VERIFICATION CODE");
        mailSender.send(message);
    }
}