package com.project.shopapp.services.customer.cart;

import com.project.shopapp.dtos.customer.cartdetail.CartDetailDto;
import com.project.shopapp.responses.cart.CartResponse;

public interface ICartService {
    CartResponse addProductToCart(Long userId , CartDetailDto cartDetailDto) throws Exception;
    Long getCartItemCount (Long userId) throws Exception;

    void clearCart (Long userId) throws Exception;
}
