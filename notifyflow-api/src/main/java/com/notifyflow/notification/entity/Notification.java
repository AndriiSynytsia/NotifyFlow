package com.notifyflow.notification.entity;

import jakarta.persistence.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipient", length = 100, nullable = false)
    private String recipient;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(name = "subject", length = 255, nullable = false)
    private String subject;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @Column(name = "scheduled_at")
    private ZonedDateTime scheduledAt;

    @Column(name = "sent_at")
    private ZonedDateTime sentAt;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "max_retries", nullable = false)
    private int maxRetries;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    protected Notification() {
    }

    public Notification(String recipient, NotificationType type, String subject, String message, ZonedDateTime scheduledAt, int maxRetries) {
        this.recipient = recipient;
        this.type = type;
        this.subject = subject;
        this.message = message;
        this.scheduledAt = scheduledAt;
        this.maxRetries = maxRetries;
        this.status = NotificationStatus.PENDING;
        this.retryCount = 0;
        this.createdAt = ZonedDateTime.now();
        this.updatedAt = ZonedDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getRecipient() {
        return recipient;
    }

    public NotificationType getType() {
        return type;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public ZonedDateTime getScheduledAt() {
        return scheduledAt;
    }

    public ZonedDateTime getSentAt() {
        return sentAt;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    //TODO: Domain methods to implement as markAsSent, markAsFailed etc.
}
