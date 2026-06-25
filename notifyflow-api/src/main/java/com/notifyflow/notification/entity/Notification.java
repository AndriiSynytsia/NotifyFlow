package com.notifyflow.notification.entity;

import jakarta.persistence.*;

import java.time.ZonedDateTime;

// Core domain entity. State transitions are enforced through domain methods only —
// no public setters to prevent the entity from reaching an inconsistent state.
@Entity
@Table(
        name = "notifications",
        indexes = {
                // Single-column indexes for status-only and scheduledAt-only queries
                @Index(name = "idx_notifications_status", columnList = "status"),
                @Index(name = "idx_notifications_scheduled_at", columnList = "scheduled_at"),
                // Composite index covers the scheduler query: WHERE status = ? AND scheduled_at <= ?
                // Without this, the scheduler query would be O(n) full table scan
                @Index(name = "idx_notifications_status_scheduled_at", columnList = "status, scheduled_at"),
                @Index(name = "idx_notifications_status_processing_started", columnList = "status, processing_started_at")
        })
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Optimistic locking — prevents lost updates when two threads process the same notification.
    // Second writer gets ObjectOptimisticLockingFailureException instead of silently overwriting.
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "recipient", length = 100, nullable = false)
    private String recipient;

    // EnumType.STRING stores "EMAIL" not ordinal 0 — safe if enum order ever changes
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "subject", length = 255, nullable = false)
    private String subject;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
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

    @Column(name = "processing_started_at")
    private ZonedDateTime processingStartedAt;

    // Protected — JPA requires a no-arg constructor but application code must use the public one
    protected Notification() {
    }

    public Notification(String recipient, NotificationType type, String subject, String message,
                        ZonedDateTime scheduledAt, int maxRetries) {
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

    public ZonedDateTime getProcessingStartedAt() {
        return processingStartedAt;
    }

    public void markAsProcessing() {
        if (status != NotificationStatus.PENDING && status != NotificationStatus.QUEUED) {
            throw new IllegalStateException("Notification can not be processed from status: " + status);
        }
        status = NotificationStatus.PROCESSING;
        processingStartedAt = ZonedDateTime.now();
        updatedAt = ZonedDateTime.now();
    }

    public void markAsSent() {
        requireProcessing();

        status = NotificationStatus.SENT;
        sentAt = ZonedDateTime.now();
        processingStartedAt = null;
        updatedAt = ZonedDateTime.now();
    }

    public void markAsFailed(String reason) {
        requireProcessing();
        retryCount++;
        failureReason = reason;
        // Reverts to PENDING for retry until retryCount reaches maxRetries, then terminally FAILED
        status = retryCount >= maxRetries ? NotificationStatus.FAILED : NotificationStatus.PENDING;
        processingStartedAt = null;
        updatedAt = ZonedDateTime.now();
    }

    private void requireProcessing() {
        if (status != NotificationStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Notification must be in PROCESSING status, current status: " + status);
        }
    }
}
