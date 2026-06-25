package com.notifyflow.delivery.provider;

import com.notifyflow.exception.NotificationDeliveryException;

public class EmailDeliveryException extends NotificationDeliveryException {
    public EmailDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
