package com.project.shopapp.services.customer.notification;


import com.project.shopapp.responses.customer.notification.NotificationResponse;

import java.util.List;

public interface INotificationService {
    List<NotificationResponse> getNotificationByUserId (Long userId) throws Exception;
    NotificationResponse markAsReadNotification(Long userId,Long notificationId) throws Exception;

    List<NotificationResponse> getUnreadNotificationsByUserId(Long userId);

    void deleteNoitificationById (Long userId,Long notificationId) throws Exception;

}
