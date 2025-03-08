package com.project.shopapp.controllers;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.CartDetailsUpdateDto;
import com.project.shopapp.models.CartDetail;
import com.project.shopapp.response.ResponseObject;
import com.project.shopapp.response.cartdetail.CartDetailResponse;
import com.project.shopapp.services.cartdetail.CartDetailService;
import com.project.shopapp.ultis.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/cartdetails")
public class CartDetailController {
    @Autowired
    private CartDetailService cartDetailService;
    @Autowired
    private LocalizationUtils localizationUtils;

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseObject> getCartDetailsByUserId(@PathVariable Long userId) throws Exception {
        try {
            List<CartDetailResponse> cartDetails = cartDetailService.getCartDetailsByUserId(userId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(cartDetails)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.CARTDETAIL_GET_CARTDETAILS_BY_USER_ID_SUCCESSFULLY))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ResponseObject> updateCartDetails(@PathVariable Long userId,
                                                            @RequestBody List<CartDetailsUpdateDto> updateCartDetails) throws Exception {
        try {
            List<CartDetailResponse> cartDetailResponses = cartDetailService.updateCartDetails(userId, updateCartDetails);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(cartDetailResponses)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.CARTDETAIL_UPDATE_SUCCESSFULLY))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }


    @DeleteMapping("/{userId}/{cartDetailId}")
    public ResponseEntity<ResponseObject> deleteCartDetailById(@PathVariable Long userId,
                                                               @PathVariable Long cartDetailId) {
        try {
            cartDetailService.deleteCartDetailById(userId, cartDetailId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.CARTDETAIL_DELETE_SUCCESSFULLY, cartDetailId))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }
}
