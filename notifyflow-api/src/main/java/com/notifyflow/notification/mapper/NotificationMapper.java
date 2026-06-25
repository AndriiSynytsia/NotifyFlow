package com.notifyflow.notification.mapper;

import com.notifyflow.notification.dto.NotificationResponseDto;
import com.notifyflow.notification.entity.Notification;
import org.springframework.stereotype.Component;

import java.util.Objects;

// =============================================================================
// EDUCATIONAL NOTES — NotificationMapper
// =============================================================================
//
// WHAT IS THIS FILE?
// The mapper is responsible for converting between the domain entity (Notification)
// and the data transfer object (NotificationResponseDto).
// It lives at the boundary between the service layer and the API layer.
//
// -----------------------------------------------------------------------------
// WHY A DEDICATED MAPPER CLASS?
// -----------------------------------------------------------------------------
// The conversion logic could live in the service, the controller, or even the
// DTO itself. But a dedicated mapper class:
//
//   1. Follows SRP — the mapper has one job: translate between types.
//   2. Is reusable — multiple services can use the same mapper.
//   3. Is testable — you can unit test the mapping logic in isolation,
//      with no database or HTTP involvement.
//   4. Centralises the mapping — if the entity or DTO changes, you update
//      one place instead of hunting through services and controllers.
//
// Alternatives to a hand-written mapper:
//   - MapStruct: annotation-based code generation, zero runtime overhead
//   - ModelMapper: reflection-based, convenient but slower at runtime
//   For a learning project, hand-written mappers make the logic explicit and
//   easy to understand. In production, MapStruct is the preferred choice.
//
// -----------------------------------------------------------------------------
// @Component
// -----------------------------------------------------------------------------
// Registers this class as a Spring bean. @Component is the generic stereotype
// annotation. You could also use @Service but that implies business logic.
// @Component signals "this is a utility/infrastructure component."
//
// Spring will inject this bean wherever NotificationMapper is declared as a
// constructor parameter (e.g. in NotificationService).
//
// -----------------------------------------------------------------------------
// Objects.requireNonNull
// -----------------------------------------------------------------------------
// This is a defensive programming technique — fail fast with a clear message
// rather than letting a NullPointerException surface somewhere deep in the
// record constructor with a confusing stack trace.
//
// Without it:
//   new NotificationResponseDto(null.getId(), ...)
//   → NullPointerException: Cannot invoke "Notification.getId()" because
//     "notification" is null   ← confusing, points to line inside this method
//
// With it:
//   Objects.requireNonNull(notification, "Notification must not be null")
//   → NullPointerException: Notification must not be null ← clear and immediate
//
// FAIL FAST principle: surface bugs as early and clearly as possible.
// O(1) — a single null check.
//
// -----------------------------------------------------------------------------
// METHOD REFERENCE — notificationMapper::toDto
// -----------------------------------------------------------------------------
// In the service layer you will see:
//   page.map(notificationMapper::toDto)
//
// notificationMapper::toDto is a method reference — a shorthand for a lambda:
//   page.map(notification -> notificationMapper.toDto(notification))
//
// Both are equivalent. Method references are preferred when the lambda just
// calls a single method with the same argument — they are more readable.
//
// -----------------------------------------------------------------------------
// BIG O — MAPPING
// -----------------------------------------------------------------------------
// toDto() is O(1) — it copies a fixed number of field references.
// No iteration, no allocation proportional to input size.
//
// When mapping a full page:
//   page.map(notificationMapper::toDto) → O(n) where n = page size
// This is acceptable because page size is bounded (e.g. max 100 records).
//
// =============================================================================

@Component
public class NotificationMapper {

    public NotificationResponseDto toDto(Notification notification) {
        Objects.requireNonNull(notification, "Notification must not be null");

        return new NotificationResponseDto(
                notification.getId(),
                notification.getRecipient(),
                notification.getType(),
                notification.getSubject(),
                notification.getMessage(),
                notification.getStatus(),
                notification.getScheduledAt(),
                notification.getSentAt(),
                notification.getRetryCount(),
                notification.getMaxRetries(),
                notification.getFailureReason(),
                notification.getCreatedAt(),
                notification.getUpdatedAt()
        );
    }
}
