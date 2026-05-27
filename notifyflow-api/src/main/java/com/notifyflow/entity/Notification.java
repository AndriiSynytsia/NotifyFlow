package com.notifyflow.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipient;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String subject;

    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private LocalDateTime scheduledAt;

    private LocalDateTime sentAt;

    private int retryCount;

    private int maxRetries;

    private String failureReason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    protected Notification() {}

    public Notification(
            String recipient,
            NotificationType type,
            String subject,
            String message,
            LocalDateTime scheduledAt,
            int maxRetries
    ){
        this.recipient = recipient;
        this.type = type;
        this.subject = subject;
        this.message = message;
        this.scheduledAt = scheduledAt;
        this.maxRetries = maxRetries;
        this.status = NotificationStatus.PENDING;
        this.retryCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public LocalDateTime getSentAt() {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    //TODO: Domain methods to implement as markAsSent, markAsFailed etc.
}
