package com.project.shopapp.services.payment;

import com.project.shopapp.dtos.payment.UpdatePaymentStatusDto;
import com.project.shopapp.response.payment.PaymentResponse;

public interface IPaymentService {
    PaymentResponse updatePaymentStatus (UpdatePaymentStatusDto statusDto) throws Exception;
}
