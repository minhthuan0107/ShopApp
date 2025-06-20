package com.project.shopapp.dtos.customer.payment;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;
import java.math.BigDecimal;
@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    @JsonProperty("payment_method")
    private String paymentMethod;
    @JsonProperty("transaction_id")
    private String transactionId;
    @JsonProperty("status")
    private String status;
}