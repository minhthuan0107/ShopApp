package com.project.shopapp.consumers;

import com.project.shopapp.dtos.admin.coupon.NotificationMessage;
import com.project.shopapp.responses.customer.notification.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {
    private final SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "coupon.notify.queue")
    public void receiveNotification(NotificationResponse notificationResponse) {
        messagingTemplate.convertAndSend("/topic/notifications/" + notificationResponse.getUserId(), notificationResponse);
    }
}
