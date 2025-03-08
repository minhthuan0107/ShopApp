package com.project.shopapp.dtos.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentRequestDto {

    @JsonProperty("amount")
    private BigDecimal amount; // Số tiền cần thanh toán
    @JsonProperty("bankCode")
    private String bankCode; // Mã ngân hàng
    @JsonProperty("language")
    private String language; // Ngôn ngữ giao diện thanh toán (vd: "vn", "en")
}
