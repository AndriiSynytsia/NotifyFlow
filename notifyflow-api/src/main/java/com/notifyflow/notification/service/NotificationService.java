package com.notifyflow.notification.service;

import com.notifyflow.delivery.dto.NotificationDeliveryAttemptDto;
import com.notifyflow.delivery.mapper.NotificationDeliveryMapper;
import com.notifyflow.delivery.repository.NotificationDeliveryAttemptRepository;
import com.notifyflow.exception.NotificationNotFoundException;
import com.notifyflow.notification.dto.NotificationCreateRequestDto;
import com.notifyflow.notification.dto.NotificationResponseDto;
import com.notifyflow.notification.entity.Notification;
import com.notifyflow.notification.entity.NotificationStatus;
import com.notifyflow.notification.mapper.NotificationMapper;
import com.notifyflow.notification.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationDeliveryAttemptRepository deliveryAttemptRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationDeliveryMapper deliveryMapper;

    public NotificationService(NotificationRepository notificationRepository, NotificationDeliveryAttemptRepository deliveryAttemptRepository, NotificationMapper notificationMapper, NotificationDeliveryMapper deliveryMapper) {
        this.notificationRepository = notificationRepository;
        this.deliveryAttemptRepository = deliveryAttemptRepository;
        this.notificationMapper = notificationMapper;
        this.deliveryMapper = deliveryMapper;
    }

    @Transactional
    public NotificationResponseDto create(NotificationCreateRequestDto request) {
        // TODO: externalise maxRetries to application config or allow per-request override
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

    // readOnly = true — Hibernate skips dirty checking, free performance win for queries
    @Transactional(readOnly = true)
    public NotificationResponseDto findById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .map(notificationMapper::toDto)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponseDto> findAll(NotificationStatus status, Pageable pageable) {
        // null status means no filter — return all notifications paginated
        Page<Notification> page = status != null
                ? notificationRepository.findByStatus(status, pageable)
                : notificationRepository.findAll(pageable);

        return page.map(notificationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<NotificationDeliveryAttemptDto> getDeliveryAttemptsByNotificationId(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new NotificationNotFoundException(notificationId);
        }

        return deliveryAttemptRepository.findByNotificationIdOrderByAttemptNumberAsc(notificationId)
                .stream()
                .map(deliveryMapper::toDto)
                .toList();
    }
}
