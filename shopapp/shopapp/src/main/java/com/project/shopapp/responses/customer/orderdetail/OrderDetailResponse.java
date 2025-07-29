package com.project.shopapp.responses.customer.orderdetail;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.OrderDetail;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailResponse {
    @JsonProperty("order_id")
    private Long orderId;
    @JsonProperty("order_detail_id")
    private Long orderDetailId;
    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("product_image")
    private String productImage;
    @JsonProperty("unit_price")
    private BigDecimal unitPrice;
    @JsonProperty("quantity")
    private int quantity;
    @JsonProperty("total_price")
    private BigDecimal totalPrice;
    public static OrderDetailResponse fromOrderDetail (OrderDetail orderDetail){
        OrderDetailResponse orderDetailResponse = OrderDetailResponse.builder()
                .orderId(orderDetail.getOrder().getId())
                .orderDetailId(orderDetail.getId())
                .productId(orderDetail.getProduct().getId())
                .productName(orderDetail.getProduct().getName())
                .productImage(orderDetail.getProduct().getUrlImage())
                .unitPrice(orderDetail.getUnitPrice())
                .quantity(orderDetail.getQuantity())
                .totalPrice(orderDetail.getTotalPrice())
                .build();
        return orderDetailResponse;
    }

}
