package com.project.shopapp.controllers.admin;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.coupon.CouponDto;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.responses.coupon.CouponResponse;
import com.project.shopapp.services.coupon.CouponService;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
@RestController("adminCouponController")
@RequestMapping("${api.prefix}/admin/coupons")
public class CouponController {
    @Autowired
    private CouponService couponService;
    @Autowired
    private LocalizationUtils localizationUtils;

    @PostMapping("create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> createCoupon(@RequestBody @Valid CouponDto couponDto,
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
            CouponResponse couponResponse = couponService.createCoupon(couponDto);
            return ResponseEntity.status(HttpStatus.CREATED).
                    body(ResponseObject.builder()
                            .status(HttpStatus.CREATED)
                            .data(couponResponse)
                            .message(localizationUtils.getLocalizedMessage(
                                    MessageKeys.COUPON_CREATED_SUCCESSFULLY))
                            .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }
}
