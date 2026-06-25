package com.notifyflow.notification.repository;

import com.notifyflow.notification.entity.Notification;
import com.notifyflow.notification.entity.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Used by GET /api/v1/notifications?status= — O(log n) via idx_notifications_status
    Page<Notification> findByStatus(NotificationStatus status, Pageable pageable);

    // Used by the scheduler to fetch the next batch of due notifications ordered oldest-first (FIFO).
    // O(log n) via composite index idx_notifications_status_scheduled_at.
    Page<Notification> findByStatusAndScheduledAtLessThanEqualOrderByScheduledAtAsc(
            NotificationStatus status, ZonedDateTime scheduledAt, Pageable pageable);

    Page<Notification> findByStatusAndProcessingStartedAtLessThanEqualOrderByProcessingStartedAtAsc(NotificationStatus status, ZonedDateTime cutoff, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Notification n SET n.processingStartedAt = :processingStartedAt WHERE n.id = :id")
    void setProcessingStartedAt(@Param("id") Long id, @Param("processingStartedAt") ZonedDateTime processingStartedAt);
}
