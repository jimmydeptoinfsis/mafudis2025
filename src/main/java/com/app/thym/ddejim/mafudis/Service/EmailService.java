package com.app.thym.ddejim.mafudis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String to, String token) {
        String resetUrl = "http://localhost:8080/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("simaflua@gmail.com"); //
        message.setTo(to);
        message.setSubject("Restablecimiento de Contraseña");
        message.setText("Para restablecer tu contraseña, haz clic en el siguiente enlace:\n" + resetUrl);

        mailSender.send(message);
    }
}