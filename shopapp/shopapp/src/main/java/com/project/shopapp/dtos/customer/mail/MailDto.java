package com.project.shopapp.dtos.customer.mail;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class MailDto implements Serializable {
    private String from = "ThuanLeShop <lethuan01072001@gmail.com>";
    private String to;
    private String subject;
    private String body;
    private String attachments;
    public MailDto ( String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }
}
