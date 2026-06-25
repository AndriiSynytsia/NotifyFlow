package com.notifyflow.delivery.provider;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SmtpEmailProviderTest {

    JavaMailSender mailSender = mock(JavaMailSender.class);
    MimeMessage mimeMessage = mock(MimeMessage.class);
    SmtpEmailProvider provider;

    @BeforeEach
    void setUp() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        provider = new SmtpEmailProvider(mailSender, "noreply@notifyflow.com");
    }

    @Test
    void delegatesToMailSender() {
        provider.send(new EmailMessage("user@example.com", "Subject", "Body", "key-1"));

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void mailExceptionIsWrapped() {
        doThrow(new MailSendException("timeout")).when(mailSender).send(any(MimeMessage.class));

        assertThatThrownBy(() -> provider.send(new EmailMessage("user@example.com", "Subject", "Body", "key-1")))
                .isInstanceOf(EmailDeliveryException.class)
                .hasMessageContaining("key-1");
    }
}
