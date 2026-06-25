# NotifyFlow

Notification & Event Processing Platform built with Java and Spring Boot.

NotifyFlow is an open-source backend project focused on building a scalable and maintainable platform for processing notifications, events, retries, scheduling, and asynchronous workflows.

---

## Requirements

- Java 21
- Maven 3.6+
- Docker & Docker Compose
- PostgreSQL (or use Docker Compose)

---

## Environment Variables

Create `.env` file in `notifyflow-api/` directory:

```bash
DB_URL=jdbc:postgresql://localhost:5433/mydatabase
DB_USERNAME=myuser
DB_PASSWORD=secret
NOTIFYFLOW_API_KEY=local-dev-key
NOTIFYFLOW_EMAIL_PROVIDER=logging
```

---

## Start Application

```bash
cd notifyflow-api
./mvnw spring-boot:run
```

This will automatically:
- Start PostgreSQL via Docker Compose
- Run Flyway migrations
- Start the application on http://localhost:8081

---

## Create Notification with curl

```bash
curl -X POST http://localhost:8081/api/v1/notifications \
  -H "Content-Type: application/json" \
  -H "X-API-Key: local-dev-key" \
  -d '{
    "recipient": "user@example.com",
    "type": "EMAIL",
    "subject": "Test Notification",
    "message": "This is a test notification",
    "scheduledAt": "2024-12-19T15:30:00Z"
  }'
```

Response includes notification `id` (e.g., `{"id": 1, ...}`).

---

## Check Notification Status

```bash
curl -H "X-API-Key: local-dev-key" \
  http://localhost:8081/api/v1/notifications/1
```

---

## Check Delivery Attempts

```bash
curl -H "X-API-Key: local-dev-key" \
  http://localhost:8081/api/v1/notifications/1/attempts
```

---

## Tech Stack

### Backend
- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA

### Database
- PostgreSQL

### Infrastructure
- Docker
- Docker Compose

### Messaging & Async
- RabbitMQ *(planned)*

### Documentation & Quality
- Swagger / OpenAPI
- JUnit & Mockito

---

## API Documentation

Once running, view API docs at: http://localhost:8081/swagger-ui.html

---

## Project Status

🚧 Early Development Stage - Core notification processing and scheduling functionality is operational.