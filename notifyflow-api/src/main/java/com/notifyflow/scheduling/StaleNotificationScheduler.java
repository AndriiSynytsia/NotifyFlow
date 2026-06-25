package com.notifyflow.scheduling;

import com.notifyflow.notification.entity.NotificationStatus;
import com.notifyflow.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class StaleNotificationScheduler {
    private static final Logger log = LoggerFactory.getLogger(StaleNotificationScheduler.class);

    private final NotificationRepository notificationRepository;
    private final StaleNotificationRecoveryService recoveryService;
    private final int batchSize;
    private final long staleThresholdMinutes;

    public StaleNotificationScheduler(NotificationRepository notificationRepository,
                                      StaleNotificationRecoveryService recoveryService,
                                      @Value("${notifyflow.recovery.batch-size:50}") int batchSize,
                                      @Value("${notifyflow.recovery.stale-threshold-minutes:10}") Long staleThresholdMinutes) {
        this.notificationRepository = notificationRepository;
        this.recoveryService = recoveryService;
        this.batchSize = batchSize;
        this.staleThresholdMinutes = staleThresholdMinutes;
    }

    @Scheduled(fixedDelayString = "${notifyflow.recovery.fixed-delay-ms:60000}")
    public void recoverStaleNotifications() {
        ZonedDateTime cutoff = ZonedDateTime.now().minusMinutes(staleThresholdMinutes);

        List<Long> ids = notificationRepository.findByStatusAndProcessingStartedAtLessThanEqualOrderByProcessingStartedAtAsc(NotificationStatus.PROCESSING, cutoff, PageRequest.of(0, batchSize))
                .map(n -> n.getId())
                .getContent();

        for (Long id : ids) {
            try {
                recoveryService.recover(id, cutoff);
            } catch (RuntimeException e) {
                log.error("Failed to recover stale notification id={}", id, e);
            }
        }
    }
}
