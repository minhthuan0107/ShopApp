    package com.project.shopapp.services.customer.email;

    import com.project.shopapp.dtos.customer.mail.MailDto;
    import jakarta.mail.MessagingException;
    import jakarta.mail.internet.MimeMessage;
    import org.springframework.amqp.rabbit.core.RabbitTemplate;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.core.io.FileSystemResource;
    import org.springframework.mail.javamail.JavaMailSender;
    import org.springframework.mail.javamail.MimeMessageHelper;
    import org.springframework.scheduling.annotation.Scheduled;
    import org.springframework.stereotype.Service;

    import java.io.File;
    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.List;

    @Service
    public class EmailService implements IEmailService {
        @Autowired
        JavaMailSender sender;
        @Autowired
        private RabbitTemplate rabbitTemplate;
        @Override
        public void send(MailDto mail) throws MessagingException, IOException {
            // Tạo message
            MimeMessage message = sender.createMimeMessage();
            // Sử dụng Helper để thiết lập các thông tin cần thiết cho message
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(mail.getFrom());
            helper.setTo(mail.getTo());
            helper.setSubject(mail.getSubject());
            helper.setText(mail.getBody(), true);
            helper.setReplyTo(mail.getFrom());

            if (mail.getAttachments() != null) {
                FileSystemResource file = new FileSystemResource(new File(mail.getAttachments()));
                helper.addAttachment(mail.getAttachments(), file);
            }
            // Gửi message đến SMTP server
            sender.send(message);
        }

        @Override
        public void queue(String to, String subject, String body) {
            queue(new MailDto(to, subject, body));
        }
        @Override
        public void queue(MailDto mail) {
            // Publish message lên RabbitMQ thay vì add vào list
            rabbitTemplate.convertAndSend("mail.exchange", "mail.send", mail);
        }
    }

