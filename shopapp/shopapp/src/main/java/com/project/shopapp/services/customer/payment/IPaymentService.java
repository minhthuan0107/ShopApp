package com.project.shopapp.services.customer.payment;

import com.project.shopapp.dtos.customer.payment.UpdatePaymentStatusDto;
import com.project.shopapp.dtos.customer.payment.UpdateTransactionDto;
import com.project.shopapp.responses.customer.payment.PaymentResponse;

public interface IPaymentService {
    PaymentResponse updatePaymentStatus (UpdatePaymentStatusDto statusDto) throws Exception;

    Long getOrderIdByTransactionId (String transactionId) throws Exception;
    PaymentResponse updatePaymentTransactionId (UpdateTransactionDto updateTransactionDto) throws Exception;

}
