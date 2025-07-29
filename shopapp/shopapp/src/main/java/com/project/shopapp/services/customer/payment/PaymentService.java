package com.project.shopapp.services.customer.payment;

import com.project.shopapp.commons.CouponStatus;
import com.project.shopapp.commons.PaymentStatus;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.customer.payment.UpdatePaymentStatusDto;
import com.project.shopapp.dtos.customer.payment.UpdateTransactionDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Payment;
import com.project.shopapp.models.UserCoupon;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.PaymentRepository;
import com.project.shopapp.repositories.UserCouponRepository;
import com.project.shopapp.responses.customer.payment.PaymentResponse;
import com.project.shopapp.services.customer.cart.CartService;
import com.project.shopapp.services.customer.email.OrderMailService;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PaymentService implements IPaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderMailService orderMailService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserCouponRepository userCouponRepository;

    @Override
    public Long getOrderIdByTransactionId(String transactionId) throws Exception {
        Long orderId = paymentRepository.findOrderIdByTransactionId(transactionId)
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(
                        MessageKeys.PAYMENT_TRANSACTION_NOT_FOUND, transactionId)));
        return orderId;
    }

    @Override
    @Transactional
    public PaymentResponse updatePaymentStatus(UpdatePaymentStatusDto statusDto) throws Exception {
        Payment payment = paymentRepository.findByTransactionId(statusDto.getTransactionId())
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(
                        MessageKeys.PAYMENT_TRANSACTION_NOT_FOUND, statusDto.getTransactionId())));
        payment.setStatus(statusDto.getStatus());
        paymentRepository.save(payment);
        // Nếu thanh toán thành công thì clear giỏ hàng
        if (statusDto.getStatus().equals(PaymentStatus.SUCCESS.name())
                && !payment.getOrder().isBuyNow()) {
            Long userId = payment.getOrder().getUser().getId();
            cartService.clearCart(userId);
        }
        //SEt lại coupon thành đã dc sử dụng đối với thanh toán vnpay
        UserCoupon existUserCoupon = userCouponRepository.findByOrderId(statusDto.getOrderId())
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(
                        MessageKeys.USERCOUPON_NOT_FOUND, statusDto.getTransactionId())));
        existUserCoupon.setStatus(CouponStatus.USED);
        existUserCoupon.setAppliedAt(LocalDate.now());
        userCouponRepository.save(existUserCoupon);
        //Gửi mail thông báo thanh toán thành công
        orderMailService.sendMailOrder(payment.getOrder());
        return PaymentResponse.fromPayment(payment);
    }

    @Override
    @Transactional
    public PaymentResponse updatePaymentTransactionId(UpdateTransactionDto statusDto) throws Exception {
        Payment payment = paymentRepository.findByOrderId(statusDto.getOrderId())
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(
                        MessageKeys.PAYMENT_ORDER_NOT_FOUND, statusDto.getTransactionId())));
        payment.setTransactionId(statusDto.getTransactionId());
        paymentRepository.save(payment);
        return PaymentResponse.fromPayment(payment);
    }
}
