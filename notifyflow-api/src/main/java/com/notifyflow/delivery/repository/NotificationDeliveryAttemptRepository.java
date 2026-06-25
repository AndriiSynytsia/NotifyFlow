package com.notifyflow.delivery.repository;

import com.notifyflow.delivery.entity.DeliveryAttemptStatus;
import com.notifyflow.delivery.entity.NotificationDeliveryAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationDeliveryAttemptRepository extends JpaRepository<NotificationDeliveryAttempt, Long> {
    List<NotificationDeliveryAttempt> findByNotificationIdOrderByAttemptNumberAsc(Long notificationId);
    Optional<NotificationDeliveryAttempt> findFirstByNotificationIdAndStatusOrderByAttemptNumberDesc(Long notificationId, DeliveryAttemptStatus status);
}

