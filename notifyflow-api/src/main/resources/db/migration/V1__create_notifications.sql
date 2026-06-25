CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT  NOT NULL DEFAULT 0,
    recipient VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    scheduled_at TIMESTAMPTZ,
    sent_at TIMESTAMPTZ,
    retry_count INT NOT NULL DEFAULT 0,
    max_retries INT NOT NULL,
    failure_reason TEXT,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    processing_started_at TIMESTAMPTZ
);

CREATE INDEX idx_notifications_status ON notifications (status);
CREATE INDEX idx_notifications_scheduled_at ON notifications (scheduled_at);
CREATE INDEX idx_notifications_status_scheduled_at ON notifications (status, scheduled_at);
CREATE INDEX idx_notifications_status_processing_started ON notifications (status, processing_started_at);