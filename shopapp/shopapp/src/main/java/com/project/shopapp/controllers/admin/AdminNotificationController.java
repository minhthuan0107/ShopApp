package com.project.shopapp.controllers.admin;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.configurations.UserDetailsImpl;
import com.project.shopapp.responses.customer.notification.NotificationResponse;
import com.project.shopapp.responses.object.ResponseObject;
import com.project.shopapp.services.admin.notification.NotificationAdminService;
import com.project.shopapp.ultis.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("${api.prefix}/admin/notifications")
public class AdminNotificationController {
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private NotificationAdminService notificationAdminService;

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getNotificationByAdminId(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        try {
            List<NotificationResponse> notificationResponses = notificationAdminService.getNotificationByAdminId(userId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .data(notificationResponses)
                            .message(localizationUtils.getLocalizedMessage(
                                    MessageKeys.NOTIFICATION_FETCHED_SUCCESSFULLY, userId))
                            .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PatchMapping("mark-as-read/{notificationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> markAsReadNotification(@PathVariable Long notificationId,
                                                                 Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        try {
            NotificationResponse notificationResponses = notificationAdminService.markAsReadNotification(userId, notificationId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .data(notificationResponses)
                            .message(localizationUtils.getLocalizedMessage(
                                    MessageKeys.NOTIFICATION_MARK_SUCCESS, notificationId))
                            .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/unread")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getUnreadNotifications(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        List<NotificationResponse> unreadNotifications = notificationAdminService.getUnreadNotificationsByAdminId(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .data(unreadNotifications)
                        .message(localizationUtils.getLocalizedMessage(
                                MessageKeys.UNREAD_NOTIFICATION_FETCHED_SUCCESSFULLY, userId))
                        .build());
    }

    @DeleteMapping("/delete/{notificationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> deleteNotificationById(@PathVariable Long notificationId,
                                                                 Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        try {
            notificationAdminService.deleteNoitificationById(userId,notificationId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message(localizationUtils.getLocalizedMessage(
                                    MessageKeys.NOTIFICATION_DELETE_SUCCESS, notificationId))
                            .build());
        } catch (Exception e){
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }
}
