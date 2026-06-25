package com.notifyflow.delivery;

import com.notifyflow.delivery.dispatcher.NotificationDispatcher;
import com.notifyflow.delivery.entity.DeliveryAttemptStatus;
import com.notifyflow.delivery.entity.NotificationDeliveryAttempt;
import com.notifyflow.delivery.processor.NotificationDeliveryProcessor;
import com.notifyflow.delivery.provider.TestEmailProviderConfig;
import com.notifyflow.delivery.repository.NotificationDeliveryAttemptRepository;
import com.notifyflow.notification.entity.Notification;
import com.notifyflow.notification.entity.NotificationStatus;
import com.notifyflow.notification.entity.NotificationType;
import com.notifyflow.exception.NotificationDeliveryException;
import com.notifyflow.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Testcontainers
@SpringBootTest(properties = {
        "spring.task.scheduling.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "notifyflow.email.provider=test"
})
@Import(TestEmailProviderConfig.class)
class NotificationDeliveryProcessorIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    NotificationDeliveryProcessor processor;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    NotificationDeliveryAttemptRepository attemptRepository;

    @MockitoBean
    NotificationDispatcher dispatcher;

    @BeforeEach
    void cleanDatabase() {
        attemptRepository.deleteAll();
        notificationRepository.deleteAll();
    }

    @Test
    void marksNotificationAndAttemptAsSuccessful() {
        Notification notification = saveNotification();

        processor.process(notification.getId());

        Notification updated = notificationRepository.findById(notification.getId()).orElseThrow();

        List<NotificationDeliveryAttempt> attempts = attemptRepository.findByNotificationIdOrderByAttemptNumberAsc(notification.getId());

        assertThat(updated.getStatus()).isEqualTo(NotificationStatus.SENT);
        assertThat(attempts).hasSize(1);
        assertThat(attempts.getFirst().getStatus()).isEqualTo(DeliveryAttemptStatus.SUCCEEDED);
    }

    @Test
    void recordsFailureAndSchedulesRetry() {
        Notification notification = saveNotification();

        doThrow(new NotificationDeliveryException("Provider unavailable"))
                .when(dispatcher)
                .dispatch(any());

        processor.process(notification.getId());

        Notification updated = notificationRepository.findById(notification.getId()).orElseThrow();

        NotificationDeliveryAttempt attempt = attemptRepository.findByNotificationIdOrderByAttemptNumberAsc(notification.getId()).getFirst();

        assertThat(updated.getStatus()).isEqualTo(NotificationStatus.PENDING);
        assertThat(updated.getRetryCount()).isEqualTo(1);
        assertThat(attempt.getStatus()).isEqualTo(DeliveryAttemptStatus.FAILED);
        assertThat(attempt.getFailureReason()).isEqualTo("Provider unavailable");
    }

    @Test
    void onlyOneWorkerDeliversNotification() throws Exception {
        Notification notification = saveNotification();
        Long notificationId = notification.getId();

        CountDownLatch start = new CountDownLatch(1);

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Future<?> first = executor.submit(() -> {
                await(start);
                processor.process(notificationId);
            });

            Future<?> second  = executor.submit(() -> {
                await(start);
                processor.process(notificationId);
            });

            start.countDown();

            first.get();
            second.get();
        }

        Notification update = notificationRepository
                .findById(notificationId)
                .orElseThrow();

        List<NotificationDeliveryAttempt> attempts = attemptRepository.findByNotificationIdOrderByAttemptNumberAsc(notificationId);

        assertThat(update.getStatus()).isEqualTo(NotificationStatus.SENT);
        assertThat(attempts).hasSize(1);
        assertThat(attempts.getFirst().getStatus()).isEqualTo(DeliveryAttemptStatus.SUCCEEDED);

        verify(dispatcher, times(1)).dispatch(any());
    }

    private void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    private Notification saveNotification() {
        return notificationRepository.saveAndFlush(new Notification(
                "test@example.com",
                NotificationType.EMAIL,
                "Test",
                "Message",
                ZonedDateTime.now(),
                3
        ));
    }
}
