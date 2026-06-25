package com.notifyflow.delivery.sender;

import com.notifyflow.delivery.processor.ClaimedNotification;
import com.notifyflow.notification.entity.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SmsNotificationSender implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(SmsNotificationSender.class);

    @Override
    public NotificationType supports() {
        return NotificationType.SMS;
    }

    @Override
    public void send(ClaimedNotification notification) {
        log.info(
                "Sending SMS notification id={} ro ewcipient={}",
                notification.id(),
                notification.recipient()
        );
    }
}
