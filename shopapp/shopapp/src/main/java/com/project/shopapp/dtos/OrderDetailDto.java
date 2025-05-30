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
}
