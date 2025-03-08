package com.project.shopapp.services.payment;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.payment.UpdatePaymentStatusDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Payment;
import com.project.shopapp.repositories.PaymentRepository;
import com.project.shopapp.response.payment.PaymentResponse;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService implements IPaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private LocalizationUtils localizationUtils;
    @Override
    @Transactional
    public PaymentResponse updatePaymentStatus(UpdatePaymentStatusDto statusDto) throws Exception{
        Payment payment = paymentRepository.findByTransactionId(statusDto.getTransactionId())
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(
                        MessageKeys.PAYMENT_TRANSACTION_NOT_FOUND,statusDto.getTransactionId())));
        payment.setStatus(statusDto.getStatus());
        paymentRepository.save(payment);
        return PaymentResponse.fromPayment(payment);
    }
}
