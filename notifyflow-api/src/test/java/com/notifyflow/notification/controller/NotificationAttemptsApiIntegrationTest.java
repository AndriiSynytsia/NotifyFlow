package com.notifyflow.notification.controller;

import com.notifyflow.delivery.provider.TestEmailProviderConfig;
import com.notifyflow.delivery.repository.NotificationDeliveryAttemptRepository;
import com.notifyflow.delivery.entity.NotificationDeliveryAttempt;
import com.notifyflow.notification.entity.Notification;
import com.notifyflow.notification.entity.NotificationType;
import com.notifyflow.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.ZonedDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@Testcontainers
@SpringBootTest(properties = {
        "spring.task.scheduling.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "notifyflow.email.provider=test"
})
@AutoConfigureMockMvc
@Import(TestEmailProviderConfig.class)
class NotificationAttemptsApiIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    MockMvc mockMvc;

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
    void returnsNotFoundWhenNotificationDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/notifications/{id}/attempts", 999999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnsEmptyListWhenNotificationExistsWithoutAttempts() throws Exception {
        Notification notification = saveNotification();

        mockMvc.perform(get("/api/v1/notifications/{id}/attempts", notification.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void returnsAttemptsOrderedByAttemptNumberWithFailureReasons() throws Exception {
        Notification notification = saveNotification();

        NotificationDeliveryAttempt first = new NotificationDeliveryAttempt(notification, 1);
        first.markFailed("SMTP timeout");
        attemptRepository.saveAndFlush(first);

        NotificationDeliveryAttempt second = new NotificationDeliveryAttempt(notification, 2);
        second.markFailed("SMTP authentication failed");
        attemptRepository.saveAndFlush(second);

        NotificationDeliveryAttempt third = new NotificationDeliveryAttempt(notification, 3);
        third.markSucceeded();
        attemptRepository.saveAndFlush(third);

        mockMvc.perform(get("/api/v1/notifications/{id}/attempts", notification.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))

                .andExpect(jsonPath("$[0].id").value(first.getId()))
                .andExpect(jsonPath("$[0].attemptNumber").value(1))
                .andExpect(jsonPath("$[0].status").value("FAILED"))
                .andExpect(jsonPath("$[0].failureReason").value("SMTP timeout"))
                .andExpect(jsonPath("$[0].startedAt").exists())
                .andExpect(jsonPath("$[0].completedAt").exists())

                .andExpect(jsonPath("$[1].id").value(second.getId()))
                .andExpect(jsonPath("$[1].attemptNumber").value(2))
                .andExpect(jsonPath("$[1].status").value("FAILED"))
                .andExpect(jsonPath("$[1].failureReason").value("SMTP authentication failed"))

                .andExpect(jsonPath("$[2].id").value(third.getId()))
                .andExpect(jsonPath("$[2].attemptNumber").value(3))
                .andExpect(jsonPath("$[2].status").value("SUCCEEDED"))
                .andExpect(jsonPath("$[2].failureReason").isEmpty());
    }

    private Notification saveNotification() {
        return notificationRepository.saveAndFlush(new Notification(
                "test@example.com",
                NotificationType.EMAIL,
                "Test subject",
                "Test message",
                ZonedDateTime.now(),
                3
        ));
    }
}