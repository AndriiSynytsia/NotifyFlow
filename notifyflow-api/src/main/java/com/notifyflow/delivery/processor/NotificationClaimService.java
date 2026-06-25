package com.notifyflow.delivery.processor;

import com.notifyflow.delivery.entity.NotificationDeliveryAttempt;
import com.notifyflow.delivery.repository.NotificationDeliveryAttemptRepository;
import com.notifyflow.notification.entity.Notification;
import com.notifyflow.notification.entity.NotificationStatus;
import com.notifyflow.notification.repository.NotificationRepository;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationClaimService {

    private final NotificationRepository repository;
    private final NotificationDeliveryAttemptRepository attemptRepository;

    public NotificationClaimService(NotificationRepository repository, NotificationDeliveryAttemptRepository attemptRepository) {
        this.repository = repository;
        this.attemptRepository = attemptRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ClaimedNotification claim(Long id) {
        Notification notification = repository.findById(id).orElseThrow();

        if (notification.getStatus() != NotificationStatus.PENDING) {
            return null;
        }

        notification.markAsProcessing();
        repository.flush();

        NotificationDeliveryAttempt attempt = attemptRepository.save(new NotificationDeliveryAttempt(
                notification,
                notification.getRetryCount() + 1
        ));

        return ClaimedNotification.from(notification, attempt.getId());
    }
}
