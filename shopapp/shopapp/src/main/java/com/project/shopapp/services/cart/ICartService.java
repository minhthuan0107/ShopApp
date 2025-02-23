package com.project.shopapp.services.cart;

import com.project.shopapp.dtos.CartDto;
import com.project.shopapp.models.Cart;
import com.project.shopapp.response.cart.CartResponse;

public interface ICartService {
    CartResponse createCart (Long userId) throws Exception;
}
