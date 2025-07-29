package com.project.shopapp.controllers.user;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.configurations.UserDetailsImpl;
import com.project.shopapp.responses.Object.ResponseObject;
import com.project.shopapp.responses.admin.coupon.CouponResponse;
import com.project.shopapp.services.admin.coupon.CouponAdminService;
import com.project.shopapp.ultis.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/coupons")
public class CouponController {
    @Autowired
    private CouponAdminService couponService;
    @Autowired
    private LocalizationUtils localizationUtils;

    @PostMapping("apply")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseObject> checkCoupon(@RequestParam String code,
                                                      Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        try {
            CouponResponse couponResponse = couponService.checkCoupon(userId, code);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(couponResponse)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.COUPON_APPLIED_SUCCESSFULLY))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());

        }
    }
}
