package com.project.shopapp.services.payment;

import com.project.shopapp.dtos.payment.PaymentRequestDto;
import com.project.shopapp.dtos.payment.PaymentQueryDto;
import com.project.shopapp.dtos.payment.PaymentRefundDto;
import com.project.shopapp.response.payment.PaymentResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public interface IVNPayService {
    String createPaymentUrl(PaymentRequestDto paymentRequest, HttpServletRequest request);
    String queryTransaction(PaymentQueryDto paymentQueryDto, HttpServletRequest request) throws IOException;
    String refundTransaction(PaymentRefundDto refundDto) throws IOException;




}
