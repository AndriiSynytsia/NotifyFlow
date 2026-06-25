package com.notifyflow.delivery.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingEmailProvider implements EmailProvider{
    private static final Logger log = LoggerFactory.getLogger(LoggingEmailProvider.class);

    @Override
    public void send(EmailMessage message) {
        log.info("[EMAIL] to={} subject='{}' idempotencyKey={} body={}",
                message.recipient(), message.subject(), message.idempotencyKey(), message.body());
    }
}
