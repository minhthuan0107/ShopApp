package com.project.shopapp.services.admin.coupon;

import com.project.shopapp.dtos.admin.coupon.SendCouponDto;
import com.project.shopapp.dtos.customer.coupon.CouponDto;
import com.project.shopapp.responses.admin.coupon.CouponResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ICouponAdminService {
    CouponResponse createCoupon(CouponDto couponDto) throws Exception;

    CouponResponse checkCoupon(Long userId, String code) throws Exception;

    Page<CouponResponse> getAllCoupons (PageRequest pageRequest, String keyword);

    CouponResponse toggleCouponStatus(Long couponId) throws Exception;

    void sendCouponToUsers(SendCouponDto sendCouponDto);
}