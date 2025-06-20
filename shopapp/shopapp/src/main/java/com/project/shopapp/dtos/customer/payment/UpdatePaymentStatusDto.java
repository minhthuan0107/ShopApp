package com.project.shopapp.dtos.customer.payment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdatePaymentStatusDto {
    @NotNull(message = "Order ID không được để trống")
    private Long orderId;
    @NotBlank(message = "Transaction ID không được để trống")
    private String transactionId;
    @NotBlank(message = "Status không được để trống")
    private String status;

}
