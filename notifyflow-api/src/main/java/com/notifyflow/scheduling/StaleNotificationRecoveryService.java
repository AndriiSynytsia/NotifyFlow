package com.notifyflow.scheduling;

import com.notifyflow.delivery.entity.DeliveryAttemptStatus;
import com.notifyflow.delivery.repository.NotificationDeliveryAttemptRepository;
import com.notifyflow.notification.entity.Notification;
import com.notifyflow.notification.entity.NotificationStatus;
import com.notifyflow.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class StaleNotificationRecoveryService {

    private static final Logger log = LoggerFactory.getLogger(StaleNotificationRecoveryService.class);
    private static final String STALE_REASON = "Recovered from stale PROCESSING state";

    private final NotificationRepository repository;
    private final NotificationDeliveryAttemptRepository attemptRepository;

    public StaleNotificationRecoveryService(NotificationRepository repository,
                                            NotificationDeliveryAttemptRepository attemptRepository) {
        this.repository = repository;
        this.attemptRepository = attemptRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recover(Long id, ZonedDateTime cutoff) {
        Notification notification = repository.findById(id).orElse(null);

        if (notification == null
                || notification.getStatus() != NotificationStatus.PROCESSING
                || notification.getProcessingStartedAt() == null
                || notification.getProcessingStartedAt().isAfter(cutoff)) {
            return;
        }

        attemptRepository.findFirstByNotificationIdAndStatusOrderByAttemptNumberDesc(id, DeliveryAttemptStatus.STARTED)
                .ifPresent(a -> a.markFailed(STALE_REASON));

        notification.markAsFailed(STALE_REASON);

        log.warn("Recovered stale notification id={} retryCount={} status={}",
                id, notification.getRetryCount(), notification.getStatus());
    }
}
