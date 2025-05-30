package com.project.shopapp.services.payment;

import com.project.shopapp.dtos.payment.UpdatePaymentStatusDto;
import com.project.shopapp.dtos.payment.UpdateTransactionDto;
import com.project.shopapp.responses.payment.PaymentResponse;

public interface IPaymentService {
    PaymentResponse updatePaymentStatus (UpdatePaymentStatusDto statusDto) throws Exception;

    Long getOrderIdByTransactionId (String transactionId) throws Exception;
    PaymentResponse updatePaymentTransactionId (UpdateTransactionDto updateTransactionDto) throws Exception;

}
