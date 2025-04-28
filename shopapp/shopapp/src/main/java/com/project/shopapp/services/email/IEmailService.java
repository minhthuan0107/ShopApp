package com.project.shopapp.services.email;

import com.project.shopapp.dtos.MailDto;
import jakarta.mail.MessagingException;
import java.io.IOException;

public interface IEmailService {
    void run();

    void queue(String to, String subject, String body);

    void queue(MailDto mail);

    void send(MailDto mail) throws MessagingException, IOException;


}
