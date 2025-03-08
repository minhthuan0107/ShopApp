package com.project.shopapp.dtos;
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
    @JsonProperty("amount")
    @Min(value = 0, message = "Tổng số tiền phải lớn hơn 0")
    private BigDecimal amount;
    @JsonProperty("payment_method")
    private String paymentMethod;
    @JsonProperty("transaction_id")
    private String transactionId;
    @JsonProperty("status")
    private String status;
}