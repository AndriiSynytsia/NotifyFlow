package com.notifyflow.delivery.dto;

import com.notifyflow.delivery.entity.DeliveryAttemptStatus;

import java.time.ZonedDateTime;

public record NotificationDeliveryAttemptDto(
       Long id,
       int attemptNumber,
       DeliveryAttemptStatus status,
       String failureReason,
       ZonedDateTime startedAt,
       ZonedDateTime completedAt
) {
}
