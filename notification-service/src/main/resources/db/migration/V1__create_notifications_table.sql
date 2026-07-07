CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    incident_id UUID NOT NULL,
    recipient_email VARCHAR(255) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    retry_count INTEGER NOT NULL,
    error_message VARCHAR(1000),
    created_at TIMESTAMP NOT NULL,
    sent_at TIMESTAMP
);