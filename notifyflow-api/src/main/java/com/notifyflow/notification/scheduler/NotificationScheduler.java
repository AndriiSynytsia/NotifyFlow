package com.notifyflow.notification.scheduler;

import com.notifyflow.notification.delivery.NotificationDispatcher;
import com.notifyflow.notification.entity.Notification;
import com.notifyflow.notification.entity.NotificationStatus;
import com.notifyflow.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class NotificationScheduler {

    private final NotificationRepository notificationRepository;
    private final NotificationDispatcher notificationDispatcher;
    private final int batchSize;

    public NotificationScheduler(NotificationRepository notificationRepository, NotificationDispatcher notificationDispatcher, @Value("${notifyflow.scheduler.batch-size:50}") int batchSize) {
        this.notificationRepository = notificationRepository;
        this.notificationDispatcher = notificationDispatcher;
        this.batchSize = batchSize;
    }

    @Scheduled(fixedDelayString = "${notifyflow.scheduler.fixed-delay-ms:5000}")
    @Transactional
    public void processDueNotifications() {
        var dueNotifications = notificationRepository.findByStatusAndScheduledAtLessThanEqualOrderByScheduledAtAsc(
                NotificationStatus.PENDING,
                ZonedDateTime.now(),
                PageRequest.of(0, batchSize)
        );

        for (Notification notification : dueNotifications) {
            try {
                notification.markAsProcessing();
                notificationDispatcher.dispatch(notification);
                notification.markAsSent();
            } catch (Exception e) {
                notification.markAsFailed(e.getMessage());

            }
        }
    }

}
