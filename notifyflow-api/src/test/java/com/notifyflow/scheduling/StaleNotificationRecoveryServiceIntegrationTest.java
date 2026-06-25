package com.notifyflow.scheduling;

import com.notifyflow.delivery.entity.DeliveryAttemptStatus;
import com.notifyflow.delivery.entity.NotificationDeliveryAttempt;
import com.notifyflow.delivery.provider.TestEmailProviderConfig;
import com.notifyflow.delivery.repository.NotificationDeliveryAttemptRepository;
import com.notifyflow.notification.entity.Notification;
import com.notifyflow.notification.entity.NotificationStatus;
import com.notifyflow.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(properties = {
        "spring.task.scheduling.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "notifyflow.email.provider=test"
})
@Import({TestEmailProviderConfig.class, NotificationTestHelper.class})
class StaleNotificationRecoveryServiceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");
    private final ZonedDateTime staleCutoff = ZonedDateTime.now().plusMinutes(10);
    @Autowired
    NotificationTestHelper notificationTestHelper;
    @Autowired
    StaleNotificationRecoveryService recoveryService;
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    NotificationDeliveryAttemptRepository attemptRepository;

    @BeforeEach
    void cleanDatabase() {
        attemptRepository.deleteAll();
        notificationRepository.deleteAll();
    }

    @Test
    void staleProcessingNotificationReturnsToPending() {
        Notification notification = notificationTestHelper.saveProcessingNotification(ZonedDateTime.now().minusMinutes(15), 3);
        notificationTestHelper.saveStartedAttempt(notification);

        recoveryService.recover(notification.getId(), staleCutoff);

        Notification updated = notificationRepository.findById(notification.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(NotificationStatus.PENDING);
        assertThat(updated.getRetryCount()).isEqualTo(1);
    }

    @Test
    void activeStartedAttemptBecomeFailed() {
        Notification notification = notificationTestHelper.saveProcessingNotification(ZonedDateTime.now().minusMinutes(15), 3);
        NotificationDeliveryAttempt attempt = notificationTestHelper.saveStartedAttempt(notification);

        recoveryService.recover(notification.getId(), staleCutoff);

        NotificationDeliveryAttempt updated = attemptRepository.findById(attempt.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(DeliveryAttemptStatus.FAILED);
        assertThat(updated.getFailureReason()).isEqualTo("Recovered from stale PROCESSING state");
    }

    @Test
    void freshProcessingNotificationIsNotRecovered() {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime cutoff = now.minusMinutes(10); // cutoff is 10 minutes ago
        ZonedDateTime processingStartedAt = now;
        // processingStartedAt is after the cutoff — should be skipped
        Notification notification = notificationTestHelper.saveProcessingNotification(processingStartedAt, 3);
        notificationTestHelper.saveStartedAttempt(notification);

        recoveryService.recover(notification.getId(), cutoff);

        Notification updated = notificationRepository.findById(notification.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(NotificationStatus.PROCESSING);
    }

    @Test
    void terminalRetryBecomesFailedWhenRetriesExhausted() {
        // maxRetries=1, retryCount already at 0 — one more markAsFailed hits the limit
        Notification notification = notificationTestHelper.saveProcessingNotification(ZonedDateTime.now().minusMinutes(15), 1);
        notificationTestHelper.saveStartedAttempt(notification);

        recoveryService.recover(notification.getId(), staleCutoff);

        Notification updated = notificationRepository.findById(notification.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(NotificationStatus.FAILED);
    }

    @Test
    void twoRecoveryWorkersDoNotCorruptState() throws Exception {
        Notification notification = notificationTestHelper.saveProcessingNotification(ZonedDateTime.now().minusMinutes(15), 3);
        notificationTestHelper.saveStartedAttempt(notification);
        Long id = notification.getId();

        CountDownLatch start = new CountDownLatch(1);

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Future<?> first = executor.submit(() -> {
                await(start);
                try {
                    recoveryService.recover(id, staleCutoff);
                } catch (ObjectOptimisticLockingFailureException ignored) {
                }
            });

            Future<?> second = executor.submit(() -> {
                await(start);
                try {
                    recoveryService.recover(id, staleCutoff);
                } catch (ObjectOptimisticLockingFailureException ignored) {
                }
            });

            start.countDown();
            first.get();
            second.get();
        }

        Notification updated = notificationRepository.findById(id).orElseThrow();
        List<NotificationDeliveryAttempt> attempts = attemptRepository.findByNotificationIdOrderByAttemptNumberAsc(id);

        // exactly one recovery happened — retryCount incremented once, one attempt marked failed
        assertThat(updated.getRetryCount()).isEqualTo(1);
        assertThat(attempts).hasSize(1);
        assertThat(attempts.getFirst().getStatus()).isEqualTo(DeliveryAttemptStatus.FAILED);
    }

    @Test
    void onlyLatestStartedAttemptIsMarkedFailed() {
        Notification notification = notificationTestHelper.saveProcessingNotification(ZonedDateTime.now().minusMinutes(15), 3);
        NotificationDeliveryAttempt olderAttempt = attemptRepository.saveAndFlush(
                new NotificationDeliveryAttempt(notification, 1));
        olderAttempt.markFailed("previous failure");
        attemptRepository.saveAndFlush(olderAttempt);

        NotificationDeliveryAttempt latestAttempt = attemptRepository.saveAndFlush(
                new NotificationDeliveryAttempt(notification, 2));

        recoveryService.recover(notification.getId(), staleCutoff);

        assertThat(attemptRepository.findById(olderAttempt.getId()).orElseThrow().getFailureReason())
                .isEqualTo("previous failure");
        assertThat(attemptRepository.findById(latestAttempt.getId()).orElseThrow().getStatus())
                .isEqualTo(DeliveryAttemptStatus.FAILED);
    }

    @Test
    void alreadyFailedAttemptIsNotTouchedOnReplay() {
        Notification notification = notificationTestHelper.saveProcessingNotification(ZonedDateTime.now().minusMinutes(15), 3);
        NotificationDeliveryAttempt attempt = notificationTestHelper.saveStartedAttempt(notification);

        recoveryService.recover(notification.getId(), staleCutoff);
        // second call — notification is now PENDING, guards should bail out
        recoveryService.recover(notification.getId(), staleCutoff);

        NotificationDeliveryAttempt updated = attemptRepository.findById(attempt.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(DeliveryAttemptStatus.FAILED);
        assertThat(updated.getFailureReason()).isEqualTo("Recovered from stale PROCESSING state");
    }


    private void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}
