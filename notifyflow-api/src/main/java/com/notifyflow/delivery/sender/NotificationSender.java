package com.notifyflow.delivery.sender;

import com.notifyflow.delivery.processor.ClaimedNotification;
import com.notifyflow.notification.entity.Notification;
import com.notifyflow.notification.entity.NotificationType;

public interface NotificationSender {
    NotificationType supports();
    void send(ClaimedNotification notification);
}
