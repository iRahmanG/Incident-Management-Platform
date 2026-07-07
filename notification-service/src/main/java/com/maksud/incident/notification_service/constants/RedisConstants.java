package com.maksud.incident.notification_service.constants;

import java.time.Duration;

public final class RedisConstants {

    private RedisConstants(){}

    public static final String NOTIFICATION_LOCK =
            "lock:notification-processor";

    public static final Duration LOCK_TIMEOUT =
            Duration.ofSeconds(30);

}
