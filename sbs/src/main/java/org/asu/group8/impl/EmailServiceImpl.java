package org.asu.group8.impl;

import org.asu.group8.config.Constants;
import org.asu.group8.entity.SbsUser;
import org.asu.group8.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.activation.DataSource;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendEmail(SbsUser sbsUser, String subject, String text) {
        // send the email on a different thread
        new Thread(() -> {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(Constants.EMAIL_FROM);
            simpleMailMessage.setTo(sbsUser.getEmail());
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(text);
            javaMailSender.send(simpleMailMessage);
        }).start();
    }

    @Override
    public void sendEmailWithAttachments(SbsUser sbsUser, String subject, String text, DataSource dataSources[], String fileNames[]) {
        // send the email on a different thread
        new Thread(() -> {
        }).start();
    }
}
