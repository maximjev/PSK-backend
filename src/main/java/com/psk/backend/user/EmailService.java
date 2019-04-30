package com.psk.backend.user;

import io.atlassian.fugue.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.net.http.HttpRequest;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public SimpleMailMessage constructResetTokenEmail(
            String contextPath,  String token, User user) {
        String url = contextPath + "/user/changePassword?id=" +
                user.getId() + "&token=" + token;

        return constructEmail("Reset Password", url, user);
    }

    public SimpleMailMessage constructEmail(String subject, String body,
                                             User user) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        return email;
    }
    public void send(SimpleMailMessage mail){
        emailSender.send(mail);
    }
}
