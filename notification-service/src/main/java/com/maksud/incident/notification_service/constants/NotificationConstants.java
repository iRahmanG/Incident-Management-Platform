package com.maksud.incident.notification_service.constants;

public final class NotificationConstants {

    public static final String INCIDENT_EVENTS_TOPIC = "incident-events";

    public static final String INCIDENT_EVENTS_DLQ_TOPIC = "incident-events-dlq";
    public static final Integer MAX_RETRY_COUNT = 3;

    private NotificationConstants() {}
}
