package com.project.shopapp.dtos.customer.cartdetail;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartDetailsUpdateDto {
    @NotNull(message = "cart_detail_id không được để trống")
    @JsonProperty("cart_detail_id")
    private Long cartDetailId;

    @NotNull(message = "newQuantity không được để trống")
    @Min(value = 1, message = "newQuantity phải > 0")
    @JsonProperty("new_quantity")
    private int newQuantity;

}
