package com.notifyflow.delivery.provider;

public record EmailMessage(
        String recipient,
        String subject,
        String body,
        String idempotencyKey
) {
}
