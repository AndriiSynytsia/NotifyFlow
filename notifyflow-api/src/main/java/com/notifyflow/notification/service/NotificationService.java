package com.notifyflow.notification.service;

import com.notifyflow.notification.dto.NotificationCreateRequestDto;
import com.notifyflow.notification.dto.NotificationResponseDto;
import com.notifyflow.notification.entity.Notification;
import com.notifyflow.notification.entity.NotificationStatus;
import com.notifyflow.notification.excepiton.NotificationNotFoundException;
import com.notifyflow.notification.mapper.NotificationMapper;
import com.notifyflow.notification.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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

    @Transactional(readOnly = true)
    public NotificationResponseDto findById(Long id) {
        return notificationRepository.findById(id)
                .map(notificationMapper::toDto)
                .orElseThrow(() -> new NotificationNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponseDto> findAll(NotificationStatus status, Pageable pageable) {
        Page<Notification> page = status != null ? notificationRepository.findByStatus(status, pageable)
                : notificationRepository.findAll(pageable);

        return page.map(notificationMapper::toDto);
    }

}
