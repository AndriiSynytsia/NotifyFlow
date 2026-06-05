package com.notifyflow.notification.mapper;

import com.notifyflow.notification.dto.NotificationResponseDto;
import com.notifyflow.notification.entity.Notification;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class NotificationMapper {

    public NotificationResponseDto toDto(Notification notification) {
        Objects.requireNonNull(notification, "Notification must not be null");

        return new NotificationResponseDto(
                notification.getId(),
                notification.getRecipient(),
                notification.getType(),
                notification.getSubject(),
                notification.getMessage(),
                notification.getStatus(),
                notification.getScheduledAt(),
                notification.getSentAt(),
                notification.getRetryCount(),
                notification.getMaxRetries(),
                notification.getFailureReason(),
                notification.getCreatedAt(),
                notification.getUpdatedAt()
        );
    }
}
