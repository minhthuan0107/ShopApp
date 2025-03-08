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
public class OrderDetailDto {
    @Min(value = 1, message = "ID sản phẩm phải lớn hơn 0")
    @JsonProperty("product_id")
    private Long productId;
    @Min(value = 1, message = "Số lượng sản phẩm phải lớn hơn 0")
    private int quantity;
    @Min(value = 0, message = "Đơn giá phải lớn hơn hoặc bằng 0")
    @JsonProperty("unit_price")
    private BigDecimal unitPrice;
    @Min(value = 0, message = "Thành tiền phải lớn hơn hoặc bằng 0")
    @JsonProperty("total_price")
    private BigDecimal totalPrice;
}
