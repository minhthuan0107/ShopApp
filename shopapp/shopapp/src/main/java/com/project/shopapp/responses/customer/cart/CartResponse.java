package com.project.shopapp.responses.customer.cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.commons.CartStatus;
import com.project.shopapp.models.BaseEntity;
import com.project.shopapp.models.Cart;
import com.project.shopapp.responses.customer.cartdetail.CartDetailResponse;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CartResponse extends BaseEntity {
    @JsonProperty("cart_id")
    private Long cartId;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("total_price")
    private BigDecimal totalPrice;
    @JsonProperty("total_quantity")
    private Integer totalQuantity;
    private CartStatus status;
    private List<CartDetailResponse> cartDetails;

    public static CartResponse fromCart (Cart cart,List<CartDetailResponse> productList){
        CartResponse cartResponse = CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUser().getId())
                .userName(cart.getUser().getFullname())
                .totalPrice(cart.getTotalPrice())
                .totalQuantity(cart.getTotalQuantity())
                .status(cart.getStatus())
                .cartDetails(productList)
                .build();
        cartResponse.setCreateAt(cart.getCreateAt());
        cartResponse.setUpdateAt(cart.getUpdateAt());
        return cartResponse;
    }
}
