package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDto {
    @Min(value = 1,message = "Order's ID must be >0")
    @JsonProperty("order_id")
    private Long oderId;
    @Min(value = 1,message = "Product's ID must be >0")
    @JsonProperty("product_id")
    private Long productId;
    @Min(value = 0,message = "Price's must be >0=")
    private Float price;
    @Min(value = 1,message = "Number of Product is must be >0")
    @JsonProperty("number_of_products")
    private int numberOfProducts;
    @Min(value = 0,message = "Total money must be >=0")
    @JsonProperty("total_money")
    private float totalMoney;
    private String color;
}
