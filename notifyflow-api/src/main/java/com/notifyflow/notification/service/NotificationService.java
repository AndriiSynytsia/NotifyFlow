package com.notifyflow.notification.service;

import com.notifyflow.notification.dto.NotificationCreateRequestDto;
import com.notifyflow.notification.dto.NotificationResponseDto;
import com.notifyflow.notification.entity.Notification;
import com.notifyflow.notification.mapper.NotificationMapper;
import com.notifyflow.notification.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationRepository notificationRepository, NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    @Transactional
    public NotificationResponseDto create(NotificationCreateRequestDto request) {
        Notification notification = new Notification(
                request.recipient(),
                request.type(),
                request.subject(),
                request.message(),
                request.scheduledAt(),
                3
        );

        Notification saved = notificationRepository.save(notification);
        return notificationMapper.toDto(saved);
    }
}
