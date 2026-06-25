package com.notifyflow.delivery.sender;

import com.notifyflow.delivery.processor.ClaimedNotification;
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
    public void send(ClaimedNotification notification) {
        log.info(
                "Sending WEBHOOK notification id={} to recipient={}",
                notification.id(),
                notification.recipient()
        );
    }
}
