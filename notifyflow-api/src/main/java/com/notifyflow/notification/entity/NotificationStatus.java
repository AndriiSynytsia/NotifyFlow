package com.notifyflow.notification.entity;

// =============================================================================
// EDUCATIONAL NOTES — NotificationStatus
// =============================================================================
//
// WHAT IS THIS FILE?
// An enum (enumeration) is a special Java type that defines a fixed set of
// named constants. NotificationStatus defines the valid states a notification
// can be in throughout its lifecycle.
//
// -----------------------------------------------------------------------------
// WHY USE AN ENUM INSTEAD OF A STRING?
// -----------------------------------------------------------------------------
// BAD approach — plain String:
//   notification.setStatus("pending"); // typo-prone, no compile-time safety
//
// GOOD approach — enum:
//   notification.setStatus(NotificationStatus.PENDING); // compiler validates this
//
// Benefits of enums:
//   - Compile-time safety: you cannot assign an invalid status
//   - IDE autocompletion: all valid values are discoverable
//   - switch/pattern matching works cleanly with enums
//   - No magic strings scattered across the codebase
//
// -----------------------------------------------------------------------------
// STATE MACHINE PATTERN
// -----------------------------------------------------------------------------
// These six statuses form a state machine — a model where an object moves
// through a defined set of states via allowed transitions:
//
//   PENDING ──────────────────────────────────────────────► PROCESSING
//      ▲                                                         │
//      │ (retry, retryCount < maxRetries)                        │
//      │                                                    success │ failure
//      └──────────────────── FAILED (terminal) ◄────────────────┘
//                                                            SENT (terminal)
//
//   PENDING   → notification is scheduled, waiting for its time to come
//   QUEUED    → notification has been picked up and placed in a queue (future RabbitMQ)
//   PROCESSING → currently being delivered (claimed by a worker)
//   SENT      → successfully delivered — terminal state, no further transitions
//   FAILED    → exhausted all retries — terminal state
//   CANCELLED → manually cancelled before delivery — terminal state
//
// WHY MODEL STATES EXPLICITLY?
// Explicit states make the system observable and debuggable.
// You can query "how many notifications are stuck in PROCESSING?" and detect
// crashed workers. Without explicit states you lose this visibility.
//
// -----------------------------------------------------------------------------
// BIG O — ENUM OPERATIONS
// -----------------------------------------------------------------------------
// Enum values are stored as a fixed-size array internally.
// Accessing a specific enum constant (NotificationStatus.PENDING) is O(1).
// Comparing two enum values with == is O(1) — reference equality, no string comparison.
// This is faster than comparing strings which is O(n) where n = string length.
//
// Always compare enums with == not .equals():
//   status == NotificationStatus.PENDING  ✓ O(1)
//   status.equals(NotificationStatus.PENDING)  ✓ also works but unnecessary overhead
//
// =============================================================================

public enum NotificationStatus {
    PENDING, QUEUED, PROCESSING, SENT, FAILED, CANCELLED
}
