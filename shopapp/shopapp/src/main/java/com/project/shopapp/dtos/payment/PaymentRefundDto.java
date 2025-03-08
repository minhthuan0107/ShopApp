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
public class PaymentRefundDto {
    @JsonProperty("transaction_type")
    private String transactionType; // Loại giao dịch hoàn tiền (vd: Refund)
    @JsonProperty("order_id")
    private String orderId; // Mã đơn hàng cần hoàn tiền
    @JsonProperty("amount")
    private BigDecimal amount; // Số tiền hoàn trả (đơn vị VND)
    @JsonProperty("transaction_date")
    private String transactionDate; // Ngày giao dịch (định dạng yyyyMMddHHmmss)
    @JsonProperty("created_by")
    private String createdBy; // Người thực hiện hoàn tiền
    @JsonProperty("ip_address")
    private String ipAddress; // Địa chỉ IP của người thực hiện giao dịch
}
