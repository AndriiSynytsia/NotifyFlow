package com.notifyflow.notification.exception;

// =============================================================================
// EDUCATIONAL NOTES — NotificationNotFoundException
// =============================================================================
//
// WHAT IS THIS FILE?
// A custom exception for the specific case when a notification with a given
// id does not exist in the database.
//
// -----------------------------------------------------------------------------
// WHY A CUSTOM EXCEPTION INSTEAD OF A GENERIC ONE?
// -----------------------------------------------------------------------------
// Option 1 — generic:
//   throw new RuntimeException("Notification not found with id: " + id);
//
// Option 2 — Spring built-in:
//   throw new ResponseStatusException(HttpStatus.NOT_FOUND, "...");
//
// Option 3 — custom (what we use):
//   throw new NotificationNotFoundException(id);
//
// Custom exceptions are preferred because:
//   - They are semantically specific — "not found" is a domain concept, not
//     just a technical error
//   - GlobalExceptionHandler can target them precisely with @ExceptionHandler
//   - They carry typed context (the id) that can be used in the message
//   - They are easy to find in a codebase — grep for NotificationNotFoundException
//   - Adding new fields (e.g. attempted lookup criteria) is straightforward
//
// -----------------------------------------------------------------------------
// EXCEPTION INHERITANCE CHAIN
// -----------------------------------------------------------------------------
//   Throwable
//     └── Exception (checked)
//           └── RuntimeException (unchecked)
//                 └── NotificationNotFoundException  ← our class
//
// By extending RuntimeException, this is an unchecked exception.
// It does not need to be declared in method signatures with "throws" and does
// not need to be caught at every call site.
// It propagates up automatically until GlobalExceptionHandler catches it.
//
// -----------------------------------------------------------------------------
// super() — PASSING THE MESSAGE TO THE PARENT
// -----------------------------------------------------------------------------
// super("Notification not found with id: " + id)
// Calls the RuntimeException(String message) constructor.
// This message is accessible via ex.getMessage() in GlobalExceptionHandler.
//
// String concatenation "Notification not found with id: " + id:
//   Java compiles this to a StringBuilder operation — O(n) where n = digits in id.
//   For a Long id this is at most 19 characters — effectively O(1).
//
// =============================================================================

public class NotificationNotFoundException extends RuntimeException {
    public NotificationNotFoundException(Long id) {
        super("Notification not found with id: " + id);
    }
}
