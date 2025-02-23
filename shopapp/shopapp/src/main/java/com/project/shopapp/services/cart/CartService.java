package com.project.shopapp.services.cart;

import com.project.shopapp.commons.CartStatus;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.CartDto;
import com.project.shopapp.models.Cart;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.CartRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.response.cart.CartResponse;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CartService implements ICartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LocalizationUtils localizationUtils;

    @Override
    @Transactional
    public CartResponse createCart(Long userId) throws Exception {
        User user = userRepository.findById(userId).
                orElseThrow(()->new RuntimeException(
                        localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND,userId)
                ));
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setStatus(CartStatus.PENDING);
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setTotalQuantity(0);
        // Lưu giỏ hàng vào database
        Cart savedCart = cartRepository.save(cart);
        // Chuyển đổi Cart thành CartResponse rồi return
        return CartResponse.fromCart(savedCart);
    }
}
