package com.project.shopapp.dtos.payment;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdatePaymentStatusDto {
    @NotBlank(message = "Transaction ID không được để trống")
    private String transactionId;
    @NotBlank(message = "Status không được để trống")
    private String status;

}
