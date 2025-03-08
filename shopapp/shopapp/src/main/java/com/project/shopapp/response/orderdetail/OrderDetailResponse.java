package com.project.shopapp.response.orderdetail;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.Cart;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.response.cart.CartResponse;
import com.project.shopapp.response.cartdetail.CartDetailResponse;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailResponse {
    @JsonProperty("order_detail_id")
    private Long orderDetailId;
    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("unit_price")
    private BigDecimal unitPrice;
    @JsonProperty("quantity")
    private int quantity;
    @JsonProperty("total_price")
    private BigDecimal totalPrice;
    public static OrderDetailResponse fromOrderDetail (OrderDetail orderDetail){
        OrderDetailResponse orderDetailResponse = OrderDetailResponse.builder()
                .orderDetailId(orderDetail.getId())
                .productId(orderDetail.getProduct().getId())
                .unitPrice(orderDetail.getUnitPrice())
                .quantity(orderDetail.getQuantity())
                .totalPrice(orderDetail.getTotalPrice())
                .build();
        return orderDetailResponse;
    }

}
