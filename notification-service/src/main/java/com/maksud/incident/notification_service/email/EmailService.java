package com.maksud.incident.notification_service.email;

import com.maksud.incident.notification_service.entity.Notification;

public interface EmailService {

    void send(Notification notification);
}
