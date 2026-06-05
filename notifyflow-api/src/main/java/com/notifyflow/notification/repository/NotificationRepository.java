package com.notifyflow.notification.repository;

import com.notifyflow.notification.entity.Notification;
import com.notifyflow.notification.entity.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByStatus(NotificationStatus status, Pageable pageable);

    Page<Notification> findByStatusAndScheduledAtLessThanEqual(NotificationStatus status, ZonedDateTime scheduledAt, Pageable pageable);
}
