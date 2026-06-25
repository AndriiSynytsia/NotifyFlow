package com.notifyflow.notification.dto;

import com.notifyflow.notification.entity.NotificationStatus;
import com.notifyflow.notification.entity.NotificationType;

import java.time.ZonedDateTime;

// =============================================================================
// EDUCATIONAL NOTES — NotificationResponseDto
// =============================================================================
//
// WHAT IS THIS FILE?
// This is the outbound DTO — it defines exactly what the API returns to the
// client in response to GET and POST requests.
//
// -----------------------------------------------------------------------------
// WHY A SEPARATE RESPONSE DTO?
// -----------------------------------------------------------------------------
// The same reason we have a request DTO: decouple the API contract from the
// internal domain model. The response DTO:
//
//   1. Controls what is exposed — you decide which fields the client sees.
//      Never expose internal fields like @Version (the optimistic lock version)
//      or JPA proxy internals.
//
//   2. Can be shaped differently from the entity — e.g. you might flatten nested
//      objects, compute derived fields, or rename fields for the API consumer.
//
//   3. Protects against accidental data leaks — if you add a sensitive field to
//      the entity later, it won't automatically appear in API responses.
//
// -----------------------------------------------------------------------------
// IMMUTABILITY OF RECORDS
// -----------------------------------------------------------------------------
// All fields in a record are final — once created, the response DTO cannot be
// modified. This is safe for HTTP responses: we build the DTO once in the mapper,
// pass it through the controller, serialise it to JSON, and it is never changed.
//
// Jackson (Spring's JSON library) can serialise records automatically.
// It calls the record's accessor methods (id(), recipient(), type()...) to
// build the JSON output.
//
// -----------------------------------------------------------------------------
// FIELD DESIGN DECISIONS
// -----------------------------------------------------------------------------
// retryCount and maxRetries are Integer (boxed) not int (primitive).
// Records can have null values for boxed types, which allows flexibility
// if these fields are ever made optional in a future API version.
//
// failureReason is nullable by design — it only has a value when status = FAILED.
// TODO: Consider a separate error response DTO or a dedicated endpoint for
// failure details, so that successful responses are not cluttered with
// a null failureReason field.
//
// sentAt is null until the notification is delivered — this is expected and
// the API consumer should handle it as an optional field.
//
// -----------------------------------------------------------------------------
// SERIALISATION — HOW RECORDS BECOME JSON
// -----------------------------------------------------------------------------
// Spring Boot uses Jackson to convert Java objects to JSON.
// A record like:
//   new NotificationResponseDto(1L, "user@example.com", EMAIL, ...)
//
// Becomes:
//   {
//     "id": 1,
//     "recipient": "user@example.com",
//     "type": "EMAIL",
//     "status": "PENDING",
//     "scheduledAt": "2025-08-01T10:00:00Z",
//     "sentAt": null,
//     ...
//   }
//
// ZonedDateTime is serialised as an ISO-8601 string by default when
// spring.jackson.serialization.write-dates-as-timestamps=false is configured.
//
// -----------------------------------------------------------------------------
// BIG O — DTO CREATION
// -----------------------------------------------------------------------------
// Creating a NotificationResponseDto is O(1) — it is just a constructor call
// that copies field references. No loops, no allocations proportional to data size.
// Mapping a Page<Notification> to Page<NotificationResponseDto> is O(n) where
// n = page size (not total records) — bounded by the page size limit.
//
// =============================================================================

public record NotificationResponseDto(
        Long id,
        String recipient,
        NotificationType type,
        String subject,
        String message,
        NotificationStatus status,
        ZonedDateTime scheduledAt,
        ZonedDateTime sentAt,
        Integer retryCount,
        Integer maxRetries,
        // Consider returning failureReason only in a dedicated failure details
        // endpoint or a separate ErrorDetailsDto to keep success responses clean.
        // TODO: This field is meaningful only when notification fails.
        String failureReason,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
) {
}
