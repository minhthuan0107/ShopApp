package com.project.shopapp.response.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.BaseEntity;
import com.project.shopapp.models.Cart;
import com.project.shopapp.models.Payment;
import com.project.shopapp.response.cart.CartResponse;
import com.project.shopapp.response.cartdetail.CartDetailResponse;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse extends BaseEntity {
    @JsonProperty("payment_id")
    private Long paymentId;
    @JsonProperty("order_id")
    private Long orderId;
    @JsonProperty("amount")
    private BigDecimal amount;
    @JsonProperty("payment_method")
    private String paymentMethod;
    @JsonProperty("transaction_id")
    private String transactionId;
    @JsonProperty("status")
    private String status;

    public static PaymentResponse fromPayment(Payment payment) {
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .transactionId(payment.getTransactionId())
                .status(payment.getStatus())
                .build();
        paymentResponse.setCreateAt(payment.getCreateAt());
        paymentResponse.setUpdateAt(payment.getUpdateAt());
        return paymentResponse;
    }
}

