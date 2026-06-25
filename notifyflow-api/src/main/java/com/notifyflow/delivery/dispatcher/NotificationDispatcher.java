package com.notifyflow.delivery.dispatcher;

import com.notifyflow.delivery.processor.ClaimedNotification;
import com.notifyflow.delivery.sender.NotificationSender;
import com.notifyflow.notification.entity.NotificationType;
import com.notifyflow.exception.NotificationDeliveryException;
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

    public void dispatch(ClaimedNotification notification) {
        NotificationSender sender = senders.get(notification.type());

        if (sender == null) {
            throw new NotificationDeliveryException("No sender configured for notification type: " + notification.type());
        }

        sender.send(notification);
    }
}
