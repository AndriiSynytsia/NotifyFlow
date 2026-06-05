package com.notifyflow.notification.dto;

import com.notifyflow.notification.entity.NotificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

public record NotificationCreateRequestDto(
        @NotBlank @Email String recipient,
        @NotNull NotificationType type,
        @NotBlank String subject,
        @NotBlank String message,
        @NotNull @FutureOrPresent ZonedDateTime scheduledAt) {

}
