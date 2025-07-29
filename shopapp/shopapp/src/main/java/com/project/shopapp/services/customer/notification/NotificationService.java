package com.project.shopapp.services.customer.notification;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Notification;
import com.project.shopapp.repositories.NotificationRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.responses.customer.notification.NotificationResponse;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService implements INotificationService {
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public List<NotificationResponse> getNotificationByUserId(Long userId) throws Exception {
        if (!userRepository.existsById(userId)) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND));
        }
        return notificationRepository.findByUserIdOrderByCreateAtDesc(userId).stream()
                .map(NotificationResponse::fromNotification)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotificationResponse markAsReadNotification(Long userId, Long notificationId) throws Exception {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(()
                -> new DataNotFoundException(
                localizationUtils.getLocalizedMessage(MessageKeys.NOTIFICATION_NOT_FOUND, notificationId))
        );
        if (!notification.getUser().getId().equals(userId)) {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.NOTIFICATION_ACCESS_DENIED));
        }
        notification.setIsRead(true);
        notificationRepository.save(notification);
        return NotificationResponse.fromNotification(notification);
    }

    @Override
    public List<NotificationResponse> getUnreadNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreateAtDesc(userId).stream()
                .map(NotificationResponse::fromNotification)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteNoitificationById(Long userId, Long notificationId) throws Exception {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(
                        MessageKeys.NOTIFICATION_NOT_FOUND, notificationId)));
        if (!notification.getUser().getId().equals(userId)) {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.NOTIFICATION_DELETE_FORBIDDEN));
        }
        notificationRepository.delete(notification);
    }
}
