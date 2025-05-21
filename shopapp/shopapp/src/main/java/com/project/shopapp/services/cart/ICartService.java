package com.project.shopapp.services.cart;

import com.project.shopapp.dtos.cartdetail.CartDetailDto;
import com.project.shopapp.responses.cart.CartResponse;

public interface ICartService {
    CartResponse addProductToCart(Long userId , CartDetailDto cartDetailDto) throws Exception;
    Long getCartItemCount (Long userId) throws Exception;

    void clearCart (Long userId) throws Exception;
}
