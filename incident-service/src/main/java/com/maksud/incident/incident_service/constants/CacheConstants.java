package com.maksud.incident.incident_service.constants;

import java.time.Duration;

public class CacheConstants {

    private CacheConstants() {}

    public static final String INCIDENT_CACHE_PREFIX = "incident:";

    public static final Duration INCIDENT_CACHE_TTL =
            Duration.ofMinutes(10);

}