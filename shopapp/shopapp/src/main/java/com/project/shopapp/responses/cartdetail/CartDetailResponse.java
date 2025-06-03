package com.project.shopapp.responses.cartdetail;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.BaseEntity;
import com.project.shopapp.models.CartDetail;
import lombok.*;

import java.math.BigDecimal;
@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
public class CartDetailResponse extends BaseEntity {
    @JsonProperty("cart_detail_id")
    private Long cartDetailId;
    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("product_image")
    private String productImage;
    @JsonProperty("product_quantity")
    private int productQuantity;
    private int quantity;
    @JsonProperty("unit_price")
    private BigDecimal unitPrice;
    @JsonProperty("total_price")
    private BigDecimal totalPrice;

    public static CartDetailResponse fromCartDetail(CartDetail cartDetail) {
        CartDetailResponse cartDetailResponse = CartDetailResponse.builder()
                .cartDetailId(cartDetail.getId())
                .productId(cartDetail.getProduct().getId())
                .productName(cartDetail.getProduct().getName())
                .productQuantity(cartDetail.getProduct().getQuantity())
                .quantity(cartDetail.getQuantity())
                .productImage(cartDetail.getProduct().getUrlImage())
                .unitPrice(cartDetail.getUnitPrice())
                .totalPrice(cartDetail.getTotalPrice())
                .build();
        cartDetailResponse.setCreateAt(cartDetail.getCreateAt());
        cartDetailResponse.setUpdateAt(cartDetail.getUpdateAt());
        return cartDetailResponse;
    }
}
