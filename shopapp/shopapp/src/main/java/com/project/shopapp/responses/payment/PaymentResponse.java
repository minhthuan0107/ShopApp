package com.project.shopapp.responses.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.BaseEntity;
import com.project.shopapp.models.Payment;
import lombok.*;

import java.math.BigDecimal;

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
    @JsonProperty("is_buy_now")
    private boolean isBuyNow;
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
                .isBuyNow(payment.getOrder().isBuyNow())
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

