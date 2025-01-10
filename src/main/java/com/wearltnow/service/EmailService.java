package com.wearltnow.service;

import com.wearltnow.util.MessageUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private MessageUtils messageUtils;

    public void sendOtpEmail(String toEmail, String otpCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(messageUtils.getAttributeMessage("yourOtpSubject"));
            helper.setFrom("WearltNow <no-reply@wearltnow.com>");

            String emailContent = messageUtils.getAttributeMessage("otpInstruction") + "<br/><br/>" +
                    "<div style='border: 2px solid #000000; padding: 10px; border-radius: 5px;'>" + // Màu khung đen
                    "<h2 style='font-size: 24px; font-weight: bold; color: #000000;'>" + otpCode + "</h2>" + // Màu chữ đen
                    "</div>";

            helper.setText(emailContent, true); // true để gửi HTML

            // Gửi email
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
