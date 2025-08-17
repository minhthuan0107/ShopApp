package com.project.shopapp.controllers.user;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.configurations.UserDetailsImpl;
import com.project.shopapp.dtos.customer.cartdetail.CartDetailsUpdateDto;
import com.project.shopapp.responses.object.ResponseObject;
import com.project.shopapp.responses.customer.cartdetail.CartDetailResponse;
import com.project.shopapp.services.customer.cartdetail.CartDetailService;
import com.project.shopapp.ultis.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/cartdetails")
public class CartDetailController {
    @Autowired
    private CartDetailService cartDetailService;
    @Autowired
    private LocalizationUtils localizationUtils;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getCartDetailsByUserId(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
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

    @PutMapping("")
    public ResponseEntity<ResponseObject> updateCartDetails(Authentication authentication,
                                                            @RequestBody List<CartDetailsUpdateDto> updateCartDetails) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
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


    @DeleteMapping("/{cartDetailId}")
    public ResponseEntity<ResponseObject> deleteCartDetailById(Authentication authentication,
                                                               @PathVariable Long cartDetailId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
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
