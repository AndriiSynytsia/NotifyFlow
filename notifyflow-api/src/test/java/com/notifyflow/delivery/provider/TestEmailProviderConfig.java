package com.notifyflow.delivery.provider;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestEmailProviderConfig {

    @Bean
    public EmailProvider emailProvider() {
        return message -> {};
    }
}
