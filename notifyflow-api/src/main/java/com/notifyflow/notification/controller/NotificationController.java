package com.notifyflow.notification.controller;

import com.notifyflow.notification.dto.NotificationCreateRequestDto;
import com.notifyflow.notification.dto.NotificationResponseDto;
import com.notifyflow.notification.entity.NotificationStatus;
import com.notifyflow.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<NotificationResponseDto> create(@Valid @RequestBody NotificationCreateRequestDto request) {
        NotificationResponseDto response = notificationService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDto> getNotificationById(@PathVariable Long id) {
        NotificationResponseDto response = notificationService.findById(id);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping()
    public ResponseEntity<Page<NotificationResponseDto>> findAll(
            @RequestParam(required = false) NotificationStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(notificationService.findAll(status, pageable));
    }
}
