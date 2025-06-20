package com.project.shopapp.services.customer.payment;

import com.project.shopapp.dtos.customer.payment.PaymentRequestDto;
import com.project.shopapp.dtos.customer.payment.PaymentQueryDto;
import com.project.shopapp.dtos.customer.payment.PaymentRefundDto;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public interface IVNPayService {
    String createPaymentUrl(PaymentRequestDto paymentRequest, HttpServletRequest request);
    String queryTransaction(PaymentQueryDto paymentQueryDto, HttpServletRequest request) throws IOException;
    String refundTransaction(PaymentRefundDto refundDto) throws IOException;




}
