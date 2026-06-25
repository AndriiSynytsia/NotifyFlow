package com.notifyflow.delivery.entity;

import com.notifyflow.notification.entity.Notification;
import jakarta.persistence.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "notification_delivery_attempt",
        uniqueConstraints = @UniqueConstraint(name = "uk_attempt_notification_number",
                columnNames = {"notification_id", "attempt_number"}),
        indexes = @Index(name = "idx_attempt_notification",
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

    protected NotificationDeliveryAttempt() {
    }

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

    public Long getId() {
        return id;
    }

    public DeliveryAttemptStatus getStatus() {
        return status;
    }

    public int getAttemptNumber() {
        return attemptNumber;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public Notification getNotification() {
        return notification;
    }

    public ZonedDateTime getStartedAt() {
        return startedAt;
    }

    public ZonedDateTime getCompletedAt() {
        return completedAt;
    }
}
