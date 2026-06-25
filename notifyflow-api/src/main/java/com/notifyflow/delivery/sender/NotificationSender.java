package com.notifyflow.delivery;

import com.notifyflow.notification.entity.Notification;
import com.notifyflow.notification.entity.NotificationType;

public interface NotificationSender {
    NotificationType supports();
    void send(Notification notification);
}
