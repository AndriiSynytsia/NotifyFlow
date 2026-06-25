package com.notifyflow.delivery;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.notifyflow.delivery.provider.EmailMessage;
import com.notifyflow.delivery.provider.SmtpEmailProvider;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.assertj.core.api.Assertions.assertThat;

class SmtpEmailProviderIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP);

    @Test
    void sendDeliversToSmtpServer() throws Exception {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("localhost");
        mailSender.setPort(ServerSetupTest.SMTP.getPort());

        SmtpEmailProvider provider = new SmtpEmailProvider(mailSender, "noreply@notifyflow.com");
        provider.send(new EmailMessage("user@example.com", "Hello", "Test body", "idem-123"));

        MimeMessage[] received = greenMail.getReceivedMessages();
        assertThat(received).hasSize(1);
        assertThat(received[0].getAllRecipients()[0].toString()).isEqualTo("user@example.com");
        assertThat(received[0].getSubject()).isEqualTo("Hello");
    }
}
