package com.project.shopapp.services.admin.notification;

import com.project.shopapp.responses.customer.notification.NotificationResponse;

import java.util.List;

public interface INotificationAdminService {
    List<NotificationResponse> getNotificationByAdminId (Long userId) throws Exception;
    NotificationResponse markAsReadNotification(Long userId,Long notificationId) throws Exception;

    List<NotificationResponse> getUnreadNotificationsByAdminId(Long userId);

    void deleteNoitificationById (Long userId,Long notificationId) throws Exception;
}
