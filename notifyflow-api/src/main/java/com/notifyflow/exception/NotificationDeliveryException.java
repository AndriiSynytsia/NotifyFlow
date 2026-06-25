package com.notifyflow.notification.exception;

// =============================================================================
// EDUCATIONAL NOTES — NotificationDeliveryException
// =============================================================================
//
// WHAT IS THIS FILE?
// A custom exception for failures that occur during the actual delivery of a
// notification — when the dispatcher or a sender encounters an error.
//
// -----------------------------------------------------------------------------
// TWO CONSTRUCTORS — WHEN TO USE EACH
// -----------------------------------------------------------------------------
// Constructor 1: message only
//   new NotificationDeliveryException("No sender configured for type: EMAIL")
//   Use when: the error is known and fully described by the message.
//   The cause (original exception) is not relevant or does not exist.
//
// Constructor 2: message + cause
//   new NotificationDeliveryException("Failed to send email", smtpException)
//   Use when: this exception wraps a lower-level exception (SMTP error, HTTP error).
//   Preserving the cause maintains the full exception chain in the stack trace,
//   which is critical for debugging production issues.
//
// ALWAYS preserve the cause when wrapping exceptions:
//   BAD:  throw new NotificationDeliveryException(e.getMessage());
//         → the original stack trace from the SMTP library is lost forever
//
//   GOOD: throw new NotificationDeliveryException("Failed to send email", e);
//         → the original exception is attached and visible in logs
//
// -----------------------------------------------------------------------------
// EXCEPTION CHAINING
// -----------------------------------------------------------------------------
// Throwable stores a "cause" — the original exception that triggered this one.
// When you log or print the stack trace, Java automatically prints the cause
// chain with "Caused by:" entries:
//
//   NotificationDeliveryException: Failed to send email
//     at NotificationDispatcher.dispatch(...)
//   Caused by: javax.mail.MessagingException: Could not connect to SMTP host
//     at ...
//
// Without chaining, you would only see NotificationDeliveryException and lose
// all context about what actually went wrong at the infrastructure level.
//
// -----------------------------------------------------------------------------
// CHECKED vs UNCHECKED — DESIGN DECISION
// -----------------------------------------------------------------------------
// NotificationDeliveryException extends RuntimeException (unchecked).
// This means senders do not need to declare "throws NotificationDeliveryException"
// in their method signatures. The exception propagates freely to the scheduler
// which catches it and calls notification.markAsFailed().
//
// If it were checked (extends Exception), every sender implementation would be
// forced to either catch it (hiding failures) or declare it (polluting interfaces).
// Unchecked is the right choice for infrastructure-level failures in Spring apps.
//
// =============================================================================

public class NotificationDeliveryException extends RuntimeException {
    public NotificationDeliveryException(String message) {
        super(message);
    }

    public NotificationDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
