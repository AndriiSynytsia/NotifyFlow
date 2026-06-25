package com.notifyflow.delivery.mapper;

import com.notifyflow.delivery.dto.NotificationDeliveryAttemptDto;
import com.notifyflow.delivery.entity.NotificationDeliveryAttempt;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class NotificationDeliveryMapper {

    public NotificationDeliveryAttemptDto toDto(NotificationDeliveryAttempt attempt) {
        Objects.requireNonNull(attempt, "Delivery attempt must not be null");

        return new NotificationDeliveryAttemptDto(
                attempt.getId(),
                attempt.getAttemptNumber(),
                attempt.getStatus(),
                attempt.getFailureReason(),
                attempt.getStartedAt(),
                attempt.getCompletedAt()
        );
    }
}
