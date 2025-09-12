package com.project.shopapp.consumers;

import com.project.shopapp.responses.customer.notification.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderConsumer {
    private final SimpMessagingTemplate messagingTemplate;
    @RabbitListener(queues = "order.notify.queue",containerFactory = "orderRabbitListenerContainerFactory")
    public void receiveNotification(NotificationResponse notificationResponse) {
        messagingTemplate.convertAndSend("/topic/order/notifications/" + notificationResponse.getUserId(), notificationResponse);
    }
}
