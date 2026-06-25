package com.notifyflow.delivery.processor;

import com.notifyflow.notification.entity.Notification;
import com.notifyflow.notification.entity.NotificationType;

public record ClaimedNotification(
        Long id,
        Long attemptId,
        String idempotencyKey,
        NotificationType type,
        String recipient,
        String subject,
        String message
) {
    public static ClaimedNotification from(Notification notification, Long attemptId) {
        return new ClaimedNotification(
                notification.getId(),
                attemptId,
                "notifyflow-notification-" + notification.getId(),
                notification.getType(),
                notification.getRecipient(),
                notification.getSubject(),
                notification.getMessage()
        );
    }
}
