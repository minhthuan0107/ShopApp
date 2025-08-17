package com.project.shopapp.controllers.admin;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.admin.coupon.SendCouponDto;
import com.project.shopapp.dtos.customer.coupon.CouponDto;
import com.project.shopapp.responses.object.ResponseObject;
import com.project.shopapp.responses.admin.coupon.CouponListResponse;
import com.project.shopapp.responses.admin.coupon.CouponResponse;
import com.project.shopapp.services.admin.coupon.CouponAdminService;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/admin/coupons")
public class AdminCouponController {
    @Autowired
    private CouponAdminService couponService;
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

    @GetMapping("/get-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getAllCoupons(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(required = false, defaultValue = "") String keyword) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<CouponResponse> couponPage = couponService.getAllCoupons(pageRequest, keyword);
        int totalPages = couponPage.getTotalPages();
        //Lấy tổng số mã giảm giá
        long totalItems = couponPage.getTotalElements();
        List<CouponResponse> couponResponseList = couponPage.getContent();
        CouponListResponse couponListResponse = CouponListResponse.builder()
                .couponResponses(couponResponseList)
                .totalPages(totalPages)
                .totalItems(totalItems)
                .currentPage(page + 1)
                .build();
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.COUPON_GET_ALL_SUCCESSFULLY))
                .data(couponListResponse)
                .build());
    }

    @PatchMapping("/toggle/{couponId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> toggleCouponStatus(@PathVariable Long couponId) {
        try {
            CouponResponse couponResponse = couponService.toggleCouponStatus(couponId);
            String message = couponResponse.isActive()
                    ? "Mã giảm giá đã được kích hoạt"
                    : "Mã giảm giá đã bị vô hiệu hóa";
            return ResponseEntity.status(HttpStatus.OK).
                    body(ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .data(couponResponse)
                            .message(message)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/send-to-all/{couponCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> sendCouponToAllUsers(@PathVariable String couponCode) {
        if (couponCode == null || couponCode.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.COUPON_REQUIRED))
                    .build());
        }
        try {
            couponService.sendCouponToAllUsers(couponCode);
            return ResponseEntity.status(HttpStatus.OK).
                    body(ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.COUPON_SENT_SUCCESSFULLY))
                            .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/send-to-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> sendCouponToUsers(@Valid @RequestBody SendCouponDto sendCouponDto,
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
            couponService.sendCouponToUsers(sendCouponDto);
            return ResponseEntity.status(HttpStatus.OK).
                    body(ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.COUPON_SENT_SUCCESSFULLY))
                            .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }
}
