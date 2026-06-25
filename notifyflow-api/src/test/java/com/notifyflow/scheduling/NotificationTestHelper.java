package com.notifyflow.scheduling;

import com.notifyflow.delivery.entity.NotificationDeliveryAttempt;
import com.notifyflow.delivery.repository.NotificationDeliveryAttemptRepository;
import com.notifyflow.notification.entity.Notification;
import com.notifyflow.notification.entity.NotificationType;
import com.notifyflow.notification.repository.NotificationRepository;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@TestComponent
public class NotificationTestHelper {

    private final NotificationRepository notificationRepository;
    private final NotificationDeliveryAttemptRepository attemptRepository;

    public NotificationTestHelper(NotificationRepository notificationRepository, NotificationDeliveryAttemptRepository attemptRepository) {
        this.notificationRepository = notificationRepository;
        this.attemptRepository = attemptRepository;
    }

    @Transactional
    public Notification saveProcessingNotification(ZonedDateTime processingStartedAt, int maxRetries) {
        Notification notification = new Notification(
                "test@example.com",
                NotificationType.EMAIL,
                "Test",
                "Message",
                ZonedDateTime.now().minusHours(1),
                maxRetries
        );
        notification.markAsProcessing();
        Notification saved = notificationRepository.saveAndFlush(notification);
        notificationRepository.setProcessingStartedAt(saved.getId(), processingStartedAt);
        notificationRepository.flush();
        return notificationRepository.findById(saved.getId()).orElseThrow();
    }

    @Transactional
    public NotificationDeliveryAttempt saveStartedAttempt(Notification notification) {
        return attemptRepository.saveAndFlush(new NotificationDeliveryAttempt(notification, 1));
    }
}
