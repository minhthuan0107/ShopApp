package com.project.shopapp.services.email;

import com.project.shopapp.dtos.MailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
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

    List<MailDto> listMail = new ArrayList<>();
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
     listMail.add(mail);
    }

    @Override
    @Scheduled(fixedDelay = 5000)
        public void run() {
            while (!listMail.isEmpty()) {
                MailDto mail = listMail.remove(0);
                try {
                    this.send(mail);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
