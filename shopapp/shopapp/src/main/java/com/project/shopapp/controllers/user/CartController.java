package com.project.shopapp.controllers.user;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.cartdetail.CartDetailDto;
import com.project.shopapp.response.ResponseObject;
import com.project.shopapp.response.cart.CartResponse;
import com.project.shopapp.services.cart.CartService;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/carts")
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private LocalizationUtils localizationUtils;

    @PostMapping("/{userId}")
    public ResponseEntity<ResponseObject> addProductToCart(@PathVariable Long userId,
                                                           @RequestBody @Valid CartDetailDto cartDetailDto,
                                                           BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(errorMessages.toString())
                    .build());
        }
        try {
            CartResponse cartResponse = cartService.addProductToCart(userId, cartDetailDto);
            return ResponseEntity.status(HttpStatus.CREATED).
                    body(ResponseObject.builder()
                    .status(HttpStatus.CREATED)
                    .data(cartResponse)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.CART_ADD_PRODUCT_SUCCESSFULLY, cartDetailDto.getProductId()))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/count/{userId}")
    public ResponseEntity<ResponseObject> getCartItemCount(@PathVariable Long userId) {
            Long cartItems = cartService.getCartItemCount(userId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(cartItems)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.CART_GET_CART_ITEMS_SUCCESSFULLY))
                    .build());
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<ResponseObject> clearCart(@PathVariable Long userId) {
        try {
            cartService.clearCart(userId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.CART_DELETE_SUCCESSFULLY, userId))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }
}
