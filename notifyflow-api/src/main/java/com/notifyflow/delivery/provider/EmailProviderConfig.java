package com.notifyflow.delivery.provider;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailProvidedConfig {

    @Bean
    @ConditionalOnProperty(name = "notifyflow.email.provider", havingValue = "logging", matchIfMissing = true)
    public EmailProvider emailProvider() {
        return new LoggingEmailProvider();
    }
}
