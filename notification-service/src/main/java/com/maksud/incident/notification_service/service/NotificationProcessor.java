package com.maksud.incident.notification_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maksud.incident.notification_service.constants.NotificationConstants;
import com.maksud.incident.notification_service.constants.RedisConstants;
import com.maksud.incident.notification_service.email.EmailService;
import com.maksud.incident.notification_service.entity.Notification;
import com.maksud.incident.notification_service.entity.NotificationStatus;
import com.maksud.incident.notification_service.event.IncidentEvent;
import com.maksud.incident.notification_service.repository.NotificationRepository;
import com.maksud.incident.notification_service.kafka.IncidentEventDlqProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProcessor {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;
    private final IncidentEventDlqProducer incidentEventDlqProducer;
    private final RedisLockService redisLockService;

    @Scheduled(fixedDelay = 10000)
    public void processPendingNotifications() {

        String ownerId = redisLockService.acquireLock(
                RedisConstants.NOTIFICATION_LOCK,
                RedisConstants.LOCK_TIMEOUT
        );

        if (ownerId == null) {
            log.info("Another instance is processing notifications.");
            return;
        }

        try {

            List<Notification> pendingNotifications =
                    notificationRepository.findByStatus(NotificationStatus.PENDING);

            if (pendingNotifications.isEmpty()) {
                return;
            }

            log.info("Found {} pending notifications", pendingNotifications.size());

            for (Notification notification : pendingNotifications) {

                try {

                    emailService.send(notification);

                    notification.setStatus(NotificationStatus.SENT);
                    notification.setSentAt(LocalDateTime.now());

                    notificationRepository.save(notification);

                    log.info("Notification {} marked as SENT", notification.getId());

                } catch (Exception e) {

                    notification.setRetryCount(notification.getRetryCount() + 1);

                    if (notification.getRetryCount() >= NotificationConstants.MAX_RETRY_COUNT) {

                        notification.setStatus(NotificationStatus.FAILED);

                        try {

                            IncidentEvent event = objectMapper.readValue(
                                    notification.getPayload(),
                                    IncidentEvent.class
                            );

                            incidentEventDlqProducer.publish(event);

                            log.warn("Notification {} moved to DLQ", notification.getId());

                        } catch (Exception ex) {

                            log.error(
                                    "Unable to publish notification {} to DLQ",
                                    notification.getId(),
                                    ex
                            );
                        }

                        log.error(
                                "Notification {} permanently failed after {} retries.",
                                notification.getId(),
                                NotificationConstants.MAX_RETRY_COUNT
                        );

                    } else {

                        notification.setStatus(NotificationStatus.PENDING);

                        log.warn(
                                "Notification {} failed. Retry {}/{}",
                                notification.getId(),
                                notification.getRetryCount(),
                                NotificationConstants.MAX_RETRY_COUNT
                        );
                    }

                    notification.setErrorMessage(e.getMessage());

                    notificationRepository.save(notification);
                }
            }

        } finally {

            redisLockService.releaseLock(
                    RedisConstants.NOTIFICATION_LOCK,
                    ownerId
            );
        }
    }
}
