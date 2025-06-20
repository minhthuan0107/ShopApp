package com.project.shopapp.services.customer.coupon;

import com.project.shopapp.dtos.customer.coupon.CouponDto;
import com.project.shopapp.responses.coupon.CouponResponse;

public interface ICouponService {
    CouponResponse createCoupon(CouponDto couponDto) throws Exception;

    CouponResponse checkCoupon(Long userId, String code) throws Exception;

}