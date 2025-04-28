package com.project.shopapp.commons;

public enum PaymentStatus {
        PENDING,   // Đang chờ thanh toán
        SUCCESS,   // Thanh toán thành công
        FAILED,    // Thanh toán thất bại
        CANCELED,  // Đã hủy
        REFUNDED;  // Đã hoàn tiền
    }