package com.project.shopapp.responses.customer.notification;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.BaseEntity;
import com.project.shopapp.models.Notification;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponse extends BaseEntity {
    @JsonProperty("notification_id")
    private Long id;
    @JsonProperty("user_id")
    private Long userId;
    private String title;
    private String content;
    private String type;
    @JsonProperty("is_read")
    private Boolean isRead;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    @JsonProperty("create_at")
    private LocalDateTime createAt;

    public static NotificationResponse fromNotification (Notification notification) {
        NotificationResponse notificationResponse = NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .createAt(notification.getCreateAt())
                .build();
        return notificationResponse;
    }
}
