package com.notifyflow.delivery;

import com.notifyflow.notification.entity.Notification;
import com.notifyflow.notification.entity.NotificationType;
import com.notifyflow.notification.exception.NotificationDeliveryException;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationDispatcher {

    private final Map<NotificationType, NotificationSender> senders;

    public NotificationDispatcher(List<NotificationSender> senderList) {
        this.senders = new EnumMap<>(NotificationType.class);

        for (NotificationSender sender : senderList) {
            NotificationSender existing = senders.put(sender.supports(), sender);

            if (existing != null) {
                throw new IllegalStateException("Duplicate notification sender for type: " + sender.supports());
            }
        }
    }

    public void dispatch(Notification notification) {
        NotificationSender sender = senders.get(notification.getType());

        if (sender == null) {
            throw new NotificationDeliveryException("No sender configured for notification type: " + notification.getType());
        }

        sender.send(notification);
    }
}
