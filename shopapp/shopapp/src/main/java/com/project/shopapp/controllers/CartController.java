package com.project.shopapp.controllers;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.response.ResponseObject;
import com.project.shopapp.response.cart.CartResponse;
import com.project.shopapp.services.cart.CartService;
import com.project.shopapp.ultis.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/carts")
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private LocalizationUtils localizationUtils;

    @PostMapping("/{userId}")
    public ResponseEntity<ResponseObject> createCart(@PathVariable Long userId) {
        try {
            CartResponse cartResponse = cartService.createCart(userId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.CREATED)
                    .data(cartResponse)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.CART_CREATED_SUCCESSFULLY))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }

}
