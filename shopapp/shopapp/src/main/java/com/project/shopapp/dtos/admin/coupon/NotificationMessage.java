package com.project.shopapp.dtos.admin.coupon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationMessage implements Serializable {
    private Long userId;
    private String couponCode;
    private String content;
}
