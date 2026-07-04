package com.maksud.incident.notification_service.service;

import com.maksud.incident.notification_service.constants.NotificationConstants;
import com.maksud.incident.notification_service.email.EmailService;
import com.maksud.incident.notification_service.entity.Notification;
import com.maksud.incident.notification_service.entity.NotificationStatus;
import com.maksud.incident.notification_service.repository.NotificationRepository;
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

    @Scheduled(fixedDelay = 10000)
    public void processPendingNotifications(){
        List<Notification> pendingNotifications = notificationRepository
                .findByStatus(NotificationStatus.PENDING);

        if(pendingNotifications.isEmpty()){
            return;
        }
        log.info("Found {} pending notifications", pendingNotifications.size());

        for(Notification notification: pendingNotifications) {
            try {
                emailService.send(notification);
                notification.setStatus(NotificationStatus.SENT);
                notification.setSentAt(LocalDateTime.now());

                notificationRepository.save(notification);

                log.info("Notification {} marked as SENT", notification.getId());

            } catch (Exception e){
                notification.setRetryCount(notification.getRetryCount() + 1);

                // Retry
                if(notification.getRetryCount() >= NotificationConstants.MAX_RETRY_COUNT){
                    notification.setStatus(NotificationStatus.FAILED);
                    log.error(
                            "Notification {} permanently failed after {} retries.",
                            notification.getId(), NotificationConstants.MAX_RETRY_COUNT
                    );
                }else {
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
    }
}
