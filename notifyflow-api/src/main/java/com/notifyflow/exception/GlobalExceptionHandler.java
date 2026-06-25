package com.notifyflow.notification.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;

// =============================================================================
// EDUCATIONAL NOTES — GlobalExceptionHandler
// =============================================================================
//
// WHAT IS THIS FILE?
// This class centralises all exception-to-HTTP-response translation in one place.
// Instead of wrapping every service call in try/catch blocks in every controller,
// exceptions bubble up naturally and this class catches them globally.
//
// -----------------------------------------------------------------------------
// @RestControllerAdvice
// -----------------------------------------------------------------------------
// A composed annotation combining:
//   @ControllerAdvice  → applies this class to ALL controllers in the application
//   @ResponseBody      → return values are serialised to JSON (same as @RestController)
//
// Without this annotation, exceptions would propagate to Spring's default error
// handling which returns a generic /error page — not useful for a REST API.
//
// This is the AOP (Aspect-Oriented Programming) principle in action:
// cross-cutting concerns (like error handling) are separated from business logic.
// No controller needs to know how errors are formatted — that concern lives here.
//
// -----------------------------------------------------------------------------
// @ExceptionHandler
// -----------------------------------------------------------------------------
// Marks a method as the handler for a specific exception type.
// When the specified exception is thrown anywhere in any controller or service
// call chain, Spring routes it to this method instead of crashing.
//
// Spring matches the most specific exception type first.
// If you have handlers for both RuntimeException and NotificationNotFoundException,
// throwing NotificationNotFoundException will call the more specific handler.
//
// -----------------------------------------------------------------------------
// handleNotFound — 404 NOT FOUND
// -----------------------------------------------------------------------------
// Called when NotificationNotFoundException is thrown (in NotificationService.findById).
// Builds an ApiErrorResponse with:
//   code    = "NOTIFICATION_NOT_FOUND"  ← machine-readable constant
//   message = the exception's message   ← "Notification not found with id: 42"
//   status  = 404
//
// ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
// Sets both the HTTP status code (404) in the response header AND the body.
//
// -----------------------------------------------------------------------------
// handleValidation — 400 BAD REQUEST
// -----------------------------------------------------------------------------
// Called when @Valid validation fails on a controller method parameter.
// MethodArgumentNotValidException contains a BindingResult with all field errors.
//
// ex.getBindingResult().getFieldErrors()
//   → returns a List<FieldError>, one per failed constraint
//
// .stream()
//   → converts the list to a Stream for functional processing
//
// .map(error -> error.getField() + ": " + error.getDefaultMessage())
//   → transforms each FieldError into a readable string
//   → e.g. "recipient: must not be blank"
//
// .findFirst()
//   → takes only the first error (we return one error at a time for simplicity)
//   → returns Optional<String>
//
// .orElse("Validation failed")
//   → fallback message if no field errors exist (unlikely but defensive)
//
// WHY RETURN ONLY THE FIRST ERROR?
// For simplicity. An alternative is to return all errors as a list — useful
// for forms where users want to see all problems at once. This is a design
// choice: single error = simpler response, all errors = better UX.
//
// -----------------------------------------------------------------------------
// EXCEPTION HIERARCHY — CHECKED vs UNCHECKED
// -----------------------------------------------------------------------------
// Java has two types of exceptions:
//
//   Checked (extends Exception):
//     Must be declared with "throws" or caught. Forces callers to handle errors.
//     Example: IOException, SQLException
//
//   Unchecked (extends RuntimeException):
//     Do not need to be declared or caught. Let exceptions propagate naturally.
//     Example: NullPointerException, IllegalStateException,
//              NotificationNotFoundException, NotificationDeliveryException
//
// In Spring applications, we use unchecked exceptions for business errors.
// They bubble up through the call stack automatically and are caught here.
// This keeps service and controller code clean — no try/catch clutter.
//
// -----------------------------------------------------------------------------
// BIG O — EXCEPTION HANDLING
// -----------------------------------------------------------------------------
// Exception handling is O(1) in the happy path — no exceptions thrown means
// this code never runs.
// When an exception is thrown, stack unwinding is O(d) where d = call stack depth.
// In a typical Spring request: controller → service → repository = ~3-10 frames.
// Effectively O(1) for practical purposes.
//
// The .stream() processing over FieldErrors is O(f) where f = number of
// validation failures — bounded by the number of fields in the DTO.
//
// =============================================================================

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NotificationNotFoundException ex) {
        ApiErrorResponse response = new ApiErrorResponse(
                "NOTIFICATION_NOT_FOUND",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                ZonedDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        ApiErrorResponse response = new ApiErrorResponse(
                "VALIDATION_FAILED",
                message,
                HttpStatus.BAD_REQUEST.value(),
                ZonedDateTime.now()
        );

        return ResponseEntity.badRequest().body(response);
    }
}
