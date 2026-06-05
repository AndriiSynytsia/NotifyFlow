package com.notifyflow.notification.delivery;

import com.notifyflow.notification.entity.Notification;
import com.notifyflow.notification.entity.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WebhookNotificationSender implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(WebhookNotificationSender.class);

    @Override
    public NotificationType supports() {
        return NotificationType.WEBHOOK;
    }

    @Override
    public void send(Notification notification) {
        log.info(
                "Sending WEBHOOK notification id={} to recipient={}",
                notification.getId(),
                notification.getRecipient()
        );
    }
}
