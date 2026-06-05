package com.notifyflow.notification.exception;


import java.time.ZonedDateTime;

public record ApiErrorResponse(
        String code,
        String message,
        int status,
        ZonedDateTime timestamp
) {
}
