package com.project.shopapp.response.orderdetail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailResponse {
    private Long id;
    @JsonProperty("order_id")
    private Long orderId;
    @JsonProperty("product_id")
    private Long productId;
    private Float price;
    @JsonProperty("number_of_products")
    private int numberOfProduct;
    @JsonProperty("total_money")
    private Float totalMoney;
    private String color;

}
