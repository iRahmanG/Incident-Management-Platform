package com.maksud.incident.notification_service.email;

import com.maksud.incident.notification_service.entity.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FakeEmailService implements EmailService{

    @Override
    public void send(Notification notification) {
//        throw new RuntimeException("SMTP Server Down");
        log.info("========== EMAIL SENT ==========");
        log.info("To      : {}", notification.getRecipientEmail());
        log.info("Subject : {}", notification.getSubject());
        log.info("Message : {}", notification.getMessage());
        log.info("================================");
    }
}
