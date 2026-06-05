package com.notifyflow.notification.controller;

import com.notifyflow.notification.dto.NotificationCreateRequestDto;
import com.notifyflow.notification.dto.NotificationResponseDto;
import com.notifyflow.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<NotificationResponseDto> create(@Valid NotificationCreateRequestDto request) {
        NotificationResponseDto response = notificationService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
