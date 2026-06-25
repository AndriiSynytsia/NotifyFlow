package com.notifyflow.delivery.sender;

import com.notifyflow.delivery.processor.ClaimedNotification;
import com.notifyflow.delivery.provider.EmailMessage;
import com.notifyflow.delivery.provider.EmailProvider;
import com.notifyflow.notification.entity.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationSender implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationSender.class);
    private final EmailProvider emailProvider;

    public EmailNotificationSender(EmailProvider emailProvided) {
        this.emailProvider = emailProvided;
    }

    @Override
    public NotificationType supports() {
        return NotificationType.EMAIL;
    }

    @Override
    public void send(ClaimedNotification notification) {
        emailProvider.send(new EmailMessage(
                notification.recipient(),
                notification.subject(),
                notification.message(),
                notification.idempotencyKey()
        ));
    }
}
