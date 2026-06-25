package com.notifyflow.scheduling;

import com.notifyflow.delivery.processor.NotificationDeliveryProcessor;
import com.notifyflow.notification.entity.Notification;
import com.notifyflow.notification.entity.NotificationStatus;
import com.notifyflow.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

// Polling scheduler — runs on fixedDelay so consecutive runs never overlap.
// Each notification is processed in its own transaction inside NotificationDeliveryProcessor.
// IDs are extracted before processing so each delivery loads a fresh entity in its own transaction.
// Batch size bounds memory to O(batchSize) regardless of how many notifications are due.
@Service
public class NotificationScheduler {

    private final NotificationRepository notificationRepository;
    private final NotificationDeliveryProcessor deliveryProcessor;
    private final int batchSize;
    private static final Logger log = LoggerFactory.getLogger(NotificationScheduler.class);

    public NotificationScheduler(NotificationRepository notificationRepository,
                                 NotificationDeliveryProcessor deliveryProcessor,
                                 // :50 is the fallback default when the property is absent
                                 @Value("${notifyflow.scheduler.batch-size:50}") int batchSize) {
        this.notificationRepository = notificationRepository;
        this.deliveryProcessor = deliveryProcessor;
        this.batchSize = batchSize;
    }

    @Scheduled(fixedDelayString = "${notifyflow.scheduler.fixed-delay-ms:5000}")
    public void processDueNotifications() {
        var ids = notificationRepository.findByStatusAndScheduledAtLessThanEqualOrderByScheduledAtAsc(
                        NotificationStatus.PENDING,
                        ZonedDateTime.now(),
                        PageRequest.of(0, batchSize)
                )
                .map(Notification::getId).getContent();

        for (Long id : ids) {
            try {
                deliveryProcessor.process(id);
            } catch (RuntimeException e) {
                // Catch per notification so one failure does not abort the rest of the batch
                log.error("Unexpected error processing notification {}", id, e);
            }
        }
    }
}
