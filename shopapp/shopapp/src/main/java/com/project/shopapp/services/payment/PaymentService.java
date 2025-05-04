package com.project.shopapp.services.payment;

import com.project.shopapp.commons.PaymentStatus;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.payment.UpdatePaymentStatusDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Payment;
import com.project.shopapp.repositories.PaymentRepository;
import com.project.shopapp.response.payment.PaymentResponse;
import com.project.shopapp.services.cart.CartService;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentService implements IPaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private CartService cartService;

    @Override
    public Long getOrderIdByTransactionId(String transactionId) throws Exception {
     Long orderId = paymentRepository.findOrderIdByTransactionId(transactionId)
             .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(
                     MessageKeys.PAYMENT_TRANSACTION_NOT_FOUND,transactionId)));
        return orderId;
    }

    @Override
    @Transactional
    public PaymentResponse updatePaymentStatus(UpdatePaymentStatusDto statusDto) throws Exception{
        Payment payment = paymentRepository.findByTransactionId(statusDto.getTransactionId())
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(
                        MessageKeys.PAYMENT_TRANSACTION_NOT_FOUND,statusDto.getTransactionId())));
        payment.setStatus(statusDto.getStatus());
        paymentRepository.save(payment);
        // Nếu thanh toán thành công thì clear giỏ hàng
        if (statusDto.getStatus().equals(PaymentStatus.SUCCESS)
                && !payment.getOrder().isBuyNow()) {
            Long userId = payment.getOrder().getUser().getId();
            cartService.clearCart(userId);
        }
        return PaymentResponse.fromPayment(payment);
    }
}
