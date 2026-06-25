package com.notifyflow.delivery.processor;

import com.notifyflow.delivery.repository.NotificationDeliveryAttemptRepository;
import com.notifyflow.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationCompletionService {
    private final NotificationRepository notificationRepository;
    private final NotificationDeliveryAttemptRepository attemptRepository;

    public NotificationCompletionService(NotificationRepository notificationRepository, NotificationDeliveryAttemptRepository attemptRepository) {
        this.notificationRepository = notificationRepository;
        this.attemptRepository = attemptRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSent(Long notificationId, Long attemptId) {
        notificationRepository.findById(notificationId).orElseThrow().markAsSent();

        attemptRepository.findById(attemptId)
                .orElseThrow()
                .markSucceeded();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(Long id, Long attemptId, String reason) {
        notificationRepository.findById(id).orElseThrow().markAsFailed(reason);

        attemptRepository.findById(attemptId)
                .orElseThrow()
                .markFailed(reason);
    }
}
