package com.notifyflow.notification.entity;

// =============================================================================
// EDUCATIONAL NOTES — NotificationType
// =============================================================================
//
// WHAT IS THIS FILE?
// NotificationType defines the supported delivery channels for notifications.
// Each value maps to a specific NotificationSender implementation in the
// delivery layer.
//
// -----------------------------------------------------------------------------
// STRATEGY PATTERN — HOW THIS ENUM DRIVES BEHAVIOR
// -----------------------------------------------------------------------------
// NotificationType is not just data — it is the key that selects a delivery
// strategy at runtime. This is the Strategy design pattern:
//
//   "Define a family of algorithms (sending strategies), encapsulate each one,
//    and make them interchangeable."
//
// The flow:
//   NotificationType.EMAIL
//        │
//        ▼
//   NotificationDispatcher (looks up sender by type)
//        │
//        ▼
//   EmailNotificationSender.send(notification)
//
// Adding a new channel (e.g. PUSH) requires:
//   1. Add PUSH to this enum
//   2. Create PushNotificationSender implements NotificationSender
//   3. Spring auto-registers it — zero changes to the dispatcher
//
// This follows the Open/Closed Principle (OCP) from SOLID:
// the system is open for extension (new channel) but closed for modification
// (existing code does not change).
//
// -----------------------------------------------------------------------------
// WHY ENUM AS A DISPATCH KEY?
// -----------------------------------------------------------------------------
// NotificationDispatcher stores senders in an EnumMap<NotificationType, NotificationSender>.
// EnumMap is a specialized Map implementation designed specifically for enum keys.
//
// BIG O — EnumMap vs HashMap:
//   EnumMap lookup: O(1) — uses the enum ordinal as a direct array index
//   HashMap lookup: O(1) average — but involves hashCode() computation and
//                   potential collision resolution
//
// EnumMap is faster in practice because it avoids hashing entirely.
// It is also more memory-efficient since the backing array size equals the
// number of enum constants (3 in this case).
//
// =============================================================================

public enum NotificationType {
    EMAIL, SMS, WEBHOOK
}
