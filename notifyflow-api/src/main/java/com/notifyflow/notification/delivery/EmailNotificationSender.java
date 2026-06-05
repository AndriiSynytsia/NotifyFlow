package com.notifyflow.notification.delivery;

import com.notifyflow.notification.entity.Notification;
import com.notifyflow.notification.entity.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationSender implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationSender.class);

    @Override
    public NotificationType supports() {
        return NotificationType.EMAIL;
    }

    @Override
    public void send(Notification notification) {
        log.info(
                "Sending EMAIL notification id={} to recipient={} subject={}",
                notification.getId(),
                notification.getRecipient(),
                notification.getSubject()
        );
    }
}
