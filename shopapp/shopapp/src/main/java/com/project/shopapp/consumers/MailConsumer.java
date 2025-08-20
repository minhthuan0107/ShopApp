package com.project.shopapp.consumers;

import com.project.shopapp.dtos.customer.mail.MailDto;
import com.project.shopapp.services.customer.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailConsumer {
    @Autowired
    private EmailService emailService;

    // Lắng nghe queue "mail.queue"
    @RabbitListener(queues = "mail.queue", containerFactory = "mailRabbitListenerContainerFactory")
    public void receiveMail(MailDto mailDto) {
        try {
            emailService.send(mailDto); // Gửi mail thực sự
            log.info("Đã gửi mail đến: {}", mailDto.getTo());
        } catch (Exception e) {
            log.error("Lỗi khi gửi mail đến {}: {}", mailDto.getTo(), e.getMessage(), e);
        }
    }
}
