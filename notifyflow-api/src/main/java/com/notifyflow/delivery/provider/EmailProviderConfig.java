package com.notifyflow.delivery.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class EmailProviderConfig {

    @Bean
    @ConditionalOnMissingBean(EmailProvider.class)
    @ConditionalOnProperty(name = "notifyflow.email.provider", havingValue = "logging", matchIfMissing = true)
    public EmailProvider loggingEmailProvider() {
        return new LoggingEmailProvider();
    }

    @Bean
    @ConditionalOnMissingBean(EmailProvider.class)
    @ConditionalOnProperty(name = "notifyflow.email.provider", havingValue = "smtp")
    public EmailProvider smtpEmailProvider(JavaMailSender mailSender,
                                           @Value("${notifyflow.email.smtp.from}") String from) {
        return new SmtpEmailProvider(mailSender, from);
    }
}
