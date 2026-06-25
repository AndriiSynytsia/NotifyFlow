package com.notifyflow.delivery;

import com.notifyflow.notification.entity.Notification;
import jakarta.persistence.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "notification_delivery_attempt",
        uniqueConstraints = @UniqueConstraint(name = "uk_attempt_notification_number",
                columnNames = {"notification_id", "attempt_number"}),
        indexes = @Index(name = "idx_attempt_notifiaction",
                columnList = "notification_id"))

public class NotificationDeliveryAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    @Column(name = "attempt_number", nullable = false)
    private int attemptNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeliveryAttemptStatus status;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "started_at", nullable = false)
    private ZonedDateTime startedAt;

    @Column(name = "completed_at")
    private ZonedDateTime completedAt;

    public NotificationDeliveryAttempt(Notification notification, int attemptNumber) {
        this.notification = notification;
        this.attemptNumber = attemptNumber;
        this.status = DeliveryAttemptStatus.STARTED;
        this.startedAt = ZonedDateTime.now();
    }

    public void markSucceeded() {
        status = DeliveryAttemptStatus.SUCCEEDED;
        completedAt = ZonedDateTime.now();
    }

    public void markFailed(String reason) {
        status = DeliveryAttemptStatus.FAILED;
        failureReason = reason;
        completedAt = ZonedDateTime.now();
    }
}
