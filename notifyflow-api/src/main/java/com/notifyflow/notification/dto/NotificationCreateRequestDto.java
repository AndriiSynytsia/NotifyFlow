package com.notifyflow.notification.dto;

import com.notifyflow.notification.entity.NotificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

// =============================================================================
// EDUCATIONAL NOTES — NotificationCreateRequestDto
// =============================================================================
//
// WHAT IS THIS FILE?
// A DTO (Data Transfer Object) is a simple object used to carry data between
// layers of the application — in this case from the HTTP request body into
// the service layer.
//
// -----------------------------------------------------------------------------
// DTO PATTERN — WHY NOT USE THE ENTITY DIRECTLY?
// -----------------------------------------------------------------------------
// It is tempting to accept a Notification entity directly in the controller:
//   @PostMapping public ResponseEntity create(@RequestBody Notification n) { ... }
//
// This is dangerous for several reasons:
//   1. Security: the client could set fields they should not control (id, status,
//      retryCount, createdAt). An attacker could send status="SENT" and skip
//      processing entirely.
//   2. Validation mismatch: the entity has DB constraints, the API has different
//      input rules (e.g. scheduledAt must be in the future for new requests,
//      but can be in the past for existing records in the DB).
//   3. Coupling: changing the entity schema forces API changes and vice versa.
//
// The DTO is the API contract — it defines exactly what the client sends.
// The entity is the storage contract — it defines what the database stores.
// Keeping them separate lets each evolve independently.
//
// -----------------------------------------------------------------------------
// JAVA RECORDS
// -----------------------------------------------------------------------------
// A record is a special class introduced in Java 16 that:
//   - Declares all fields in the header (the constructor parameters)
//   - Auto-generates: constructor, getters, equals(), hashCode(), toString()
//   - Is immutable by default — all fields are final
//
// Without record (verbose):
//   public class NotificationCreateRequestDto {
//       private final String recipient;
//       public NotificationCreateRequestDto(String recipient) { this.recipient = recipient; }
//       public String getRecipient() { return recipient; }
//       // equals, hashCode, toString...
//   }
//
// With record (concise):
//   public record NotificationCreateRequestDto(String recipient) {}
//
// For DTOs, records are ideal because DTOs carry data and should be immutable.
// Once the request is parsed, the values should not change.
//
// Access a record field with: dto.recipient() — no "get" prefix.
//
// -----------------------------------------------------------------------------
// BEAN VALIDATION ANNOTATIONS
// -----------------------------------------------------------------------------
// These annotations are processed by Spring when @Valid is placed on the
// controller method parameter. Spring validates the incoming JSON before it
// even reaches the service layer.
//
// @NotBlank
//   Fails if the value is null, empty "", or whitespace only "   ".
//   Use for String fields that must have meaningful content.
//   Note: @NotEmpty would allow "   " (spaces only), @NotBlank does not.
//
// @Email
//   Fails if the value does not match a valid email format.
//   Combined with @NotBlank: first ensures it's not blank, then validates format.
//
// @NotNull
//   Fails if the value is null. Used for non-String types (enums, objects).
//   For String use @NotBlank instead (stricter).
//
// @FutureOrPresent
//   Fails if the ZonedDateTime is in the past.
//   This enforces the business rule: you cannot schedule a notification for a
//   time that has already passed.
//
// WHAT HAPPENS WHEN VALIDATION FAILS?
// Spring throws MethodArgumentNotValidException.
// GlobalExceptionHandler catches it and returns a 400 BAD REQUEST response
// with a structured error message.
//
// -----------------------------------------------------------------------------
// BIG O — VALIDATION
// -----------------------------------------------------------------------------
// Each annotation check is O(1) — constant time regardless of data size.
// @Email involves a regex match which is O(m) where m = length of the email
// string — but email strings are short and bounded, so effectively O(1) in practice.
//
// Total validation cost: O(number of fields) — proportional to the number of
// constraints, not the size of the dataset.
//
// =============================================================================

public record NotificationCreateRequestDto(
        @NotBlank @Email String recipient,
        @NotNull NotificationType type,
        @NotBlank String subject,
        @NotBlank String message,
        @NotNull @FutureOrPresent ZonedDateTime scheduledAt) {

}
