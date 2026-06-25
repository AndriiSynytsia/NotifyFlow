package com.notifyflow.exception;


import java.time.ZonedDateTime;

// =============================================================================
// EDUCATIONAL NOTES — ApiErrorResponse
// =============================================================================
//
// WHAT IS THIS FILE?
// This record defines the structure of every error response returned by the API.
// Instead of returning a raw exception message or an inconsistent string,
// every error follows the same predictable JSON shape.
//
// -----------------------------------------------------------------------------
// WHY A STRUCTURED ERROR RESPONSE?
// -----------------------------------------------------------------------------
// Without it, different errors look different to the API consumer:
//   404: "Notification not found with id: 42"      ← just a string
//   400: "recipient: must not be blank"             ← just a string
//   500: (stack trace dump)                         ← catastrophic
//
// With ApiErrorResponse, every error has the same structure:
//   {
//     "code": "NOTIFICATION_NOT_FOUND",
//     "message": "Notification not found with id: 42",
//     "status": 404,
//     "timestamp": "2025-08-01T10:00:00Z"
//   }
//
// Benefits for API consumers (frontend, mobile apps, other services):
//   - They can always parse the same JSON shape
//   - They can switch on "code" to handle specific errors programmatically
//   - "timestamp" helps correlate errors with logs
//   - No internal stack traces leak to the outside world (security)
//
// -----------------------------------------------------------------------------
// FIELDS
// -----------------------------------------------------------------------------
// code
//   A machine-readable string constant identifying the error type.
//   Consumers can switch on this value:
//     if (error.code === "NOTIFICATION_NOT_FOUND") showNotFoundPage();
//   Using a constant string is more stable than switching on HTTP status codes,
//   since multiple error types can share the same HTTP status (e.g. multiple
//   types of 400 errors).
//
// message
//   A human-readable description of what went wrong.
//   Suitable for display in logs or developer-facing error messages.
//   Should NOT expose internal implementation details (table names, stack traces).
//
// status
//   The HTTP status code as an integer (e.g. 404, 400, 500).
//   Including it in the body is convenient — consumers don't have to read the
//   HTTP header separately.
//
// timestamp
//   When the error occurred, in ZonedDateTime (timezone-aware ISO-8601 format).
//   Useful for correlating client-reported errors with server logs.
//
// -----------------------------------------------------------------------------
// RECORD IMMUTABILITY FOR ERROR RESPONSES
// -----------------------------------------------------------------------------
// Error responses should never be mutated after creation.
// A record guarantees this — all fields are final.
// Once GlobalExceptionHandler builds an ApiErrorResponse, it cannot be changed.
//
// =============================================================================

public record ApiErrorResponse(
        String code,
        String message,
        int status,
        ZonedDateTime timestamp
) {
}
