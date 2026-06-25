package com.notifyflow.delivery.provider;

import com.notifyflow.exception.NotificationDeliveryException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class SmtpEmailProvider implements EmailProvider{

    private final JavaMailSender mailSender;
    private final String from;

    public SmtpEmailProvider(JavaMailSender mailSender, String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public void send(EmailMessage message) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime);
            helper.setFrom(from);
            helper.setTo(message.recipient());
            helper.setSubject(message.subject());
            helper.setText(message.body());
            mailSender.send(mime);
        } catch (MessagingException | MailException e) {
            throw new NotificationDeliveryException("SMTP send failed for idempotencyKey=" + message.idempotencyKey(), e);
        }
    }
}
