package com.notifyflow.repository;

import com.notifyflow.entity.Notification;
import com.notifyflow.entity.NotificationStatus;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface NotificationRepository extends JpaRepository<Long, Notification> {

    @Nullable Notification findByStatus(Long id, NotificationStatus status);

    @Nullable Notification findByStatusAndScheduledAtLessThenEqual(NotificationStatus status, LocalDateTime scheduledAt);
}
