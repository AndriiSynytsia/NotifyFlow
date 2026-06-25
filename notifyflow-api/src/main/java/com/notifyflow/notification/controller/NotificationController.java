package com.notifyflow.notification.controller;

import com.notifyflow.delivery.dto.NotificationDeliveryAttemptDto;
import com.notifyflow.notification.dto.NotificationCreateRequestDto;
import com.notifyflow.notification.dto.NotificationResponseDto;
import com.notifyflow.notification.entity.NotificationStatus;
import com.notifyflow.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// =============================================================================
// EDUCATIONAL NOTES — NotificationController
// =============================================================================
//
// WHAT IS THIS FILE?
// The controller is the HTTP entry point of the application.
// It receives HTTP requests, delegates to the service layer, and returns
// HTTP responses. It contains no business logic — only HTTP concern handling.
//
// -----------------------------------------------------------------------------
// @RestController
// -----------------------------------------------------------------------------
// A composed annotation that combines:
//   @Controller   → marks this as a Spring MVC controller (handles web requests)
//   @ResponseBody → every method return value is serialised directly to the
//                   HTTP response body as JSON (via Jackson)
//
// Without @ResponseBody, Spring would try to resolve a view template (like Thymeleaf)
// for the return value. With it, Java objects become JSON automatically.
//
// -----------------------------------------------------------------------------
// @RequestMapping("/api/v1/notifications")
// -----------------------------------------------------------------------------
// Sets the base URL path for all methods in this controller.
// All endpoints in this class are prefixed with /api/v1/notifications.
//
// WHY /api/v1/?
//   - /api       → distinguishes API endpoints from static resources or UI routes
//   - /v1        → API versioning — allows introducing /v2 with breaking changes
//                  without removing /v1 for existing consumers
// This is a REST best practice for backwards compatibility.
//
// -----------------------------------------------------------------------------
// CONSTRUCTOR INJECTION
// -----------------------------------------------------------------------------
// The controller depends only on NotificationService — not on repositories or
// mappers. This enforces the layered architecture: the controller only knows
// about the layer directly below it (the service).
//
// -----------------------------------------------------------------------------
// @PostMapping — POST /api/v1/notifications
// -----------------------------------------------------------------------------
// Maps HTTP POST requests to the create() method.
//
// @RequestBody
//   Tells Spring to deserialise the HTTP request body JSON into a
//   NotificationCreateRequestDto object using Jackson.
//
// @Valid
//   Triggers Bean Validation on the DTO before the method body executes.
//   If any @NotBlank, @Email, @NotNull, or @FutureOrPresent constraint fails,
//   Spring throws MethodArgumentNotValidException immediately.
//   The GlobalExceptionHandler catches it and returns 400 BAD REQUEST.
//   The create() method is never called with invalid data.
//
// ResponseEntity.status(HttpStatus.CREATED).body(response)
//   Returns HTTP 201 Created — the correct status for a successful resource creation.
//   HTTP status codes matter for API consumers:
//     200 OK       → successful read or update
//     201 Created  → new resource was created
//     400 Bad Request → client sent invalid data
//     404 Not Found   → resource does not exist
//     500 Internal Server Error → unexpected server-side failure
//
// -----------------------------------------------------------------------------
// @GetMapping("/{id}") — GET /api/v1/notifications/{id}
// -----------------------------------------------------------------------------
// @PathVariable Long id
//   Extracts the {id} segment from the URL path and binds it to the id parameter.
//   Example: GET /api/v1/notifications/42 → id = 42
//
//   Spring automatically converts the String "42" from the URL to a Long.
//   If the id cannot be parsed (e.g. "abc"), Spring returns 400 BAD REQUEST.
//   If the id is valid but not found, NotificationNotFoundException is thrown
//   and GlobalExceptionHandler returns 404 NOT FOUND.
//
// ResponseEntity.ok().body(response)
//   Returns HTTP 200 OK with the notification in the body.
//
// -----------------------------------------------------------------------------
// @GetMapping — GET /api/v1/notifications
// -----------------------------------------------------------------------------
// @RequestParam(required = false) NotificationStatus status
//   Binds the ?status= query parameter. required = false means the parameter
//   is optional — if omitted, status is null and all notifications are returned.
//   Spring automatically converts the String "PENDING" to the enum value.
//   If an invalid enum value is sent (e.g. ?status=INVALID), Spring returns 400.
//
// @PageableDefault(size = 20) Pageable pageable
//   Spring automatically constructs a Pageable from query parameters:
//     ?page=0&size=20&sort=scheduledAt,asc
//   @PageableDefault sets the default page size to 20 when the client omits ?size=.
//   Without a default, Spring uses size=20 anyway, but being explicit is good practice.
//
// Page<NotificationResponseDto> as return type
//   Page serialises to JSON with metadata:
//   {
//     "content": [...],         ← the actual results
//     "totalElements": 150,     ← total matching records in DB
//     "totalPages": 8,          ← ceil(150 / 20)
//     "size": 20,               ← page size
//     "number": 0               ← current page index
//   }
//
// -----------------------------------------------------------------------------
// REST DESIGN PRINCIPLES APPLIED HERE
// -----------------------------------------------------------------------------
//   - Nouns in URLs, not verbs: /notifications not /createNotification
//   - HTTP method expresses the action: POST=create, GET=read
//   - Stateless: no session state on the server, each request is self-contained
//   - Consistent status codes: 201 for creation, 200 for reads, 404 for not found
//   - Versioned API path: /v1/ allows future non-breaking evolution
//
// -----------------------------------------------------------------------------
// BIG O — CONTROLLER LAYER
// -----------------------------------------------------------------------------
// The controller itself is O(1) — it just deserialises input and delegates.
// All O(n) or O(log n) operations happen in the service and repository layers.
// The controller adds no algorithmic complexity of its own.
//
// =============================================================================

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<NotificationResponseDto> create(@Valid @RequestBody NotificationCreateRequestDto request) {
        NotificationResponseDto response = notificationService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDto> getNotificationById(@PathVariable Long id) {
        NotificationResponseDto response = notificationService.findById(id);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping()
    public ResponseEntity<Page<NotificationResponseDto>> findAll(
            @RequestParam(required = false) NotificationStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(notificationService.findAll(status, pageable));
    }

    @GetMapping("/{id}/attempts")
    public ResponseEntity<List<NotificationDeliveryAttemptDto>> getAttemptsById(@PathVariable Long id) {
        List<NotificationDeliveryAttemptDto> response = notificationService.getDeliveryAttemptsByNotificationId(id);

        return ResponseEntity.ok().body(response);
    }
}
