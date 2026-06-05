package com.notifyflow.notification.dto;

import com.notifyflow.notification.entity.NotificationStatus;
import com.notifyflow.notification.entity.NotificationType;

import java.time.ZonedDateTime;

public record NotificationResponseDto(
        Long id,
        String recipient,
        NotificationType type,
        String subject,
        String message,
        NotificationStatus status,
        ZonedDateTime scheduledAt,
        ZonedDateTime sentAt,
        Integer retryCount,
        Integer maxRetries,
        // TODO: This field meaningful when notification fails. Consider about using different approach
        String failureReason,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
) {
}
