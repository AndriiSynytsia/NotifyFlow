CREATE TABLE notification_delivery_attempt (
    id BIGSERIAL PRIMARY KEY,
    notification_id BIGINT NOT NULL REFERENCES notifications (id),
    attempt_number INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    failure_reason TEXT,
    started_at TIMESTAMPTZ NOT NULL,
    completed_at TIMESTAMPTZ,

    CONSTRAINT uk_attempt_notification_number UNIQUE (notification_id, attempt_number)
);

CREATE INDEX idx_attempt_notification ON notification_delivery_attempt (notification_id);