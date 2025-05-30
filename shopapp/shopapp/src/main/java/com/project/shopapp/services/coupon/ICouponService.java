package com.project.shopapp.services.coupon;

import com.project.shopapp.dtos.coupon.CouponDto;
import com.project.shopapp.models.Coupon;
import com.project.shopapp.responses.coupon.ApplyCouponResponse;
import com.project.shopapp.responses.coupon.CouponResponse;

public interface ICouponService {
    CouponResponse createCoupon(CouponDto couponDto) throws Exception;

    CouponResponse checkCoupon(Long userId, String code) throws Exception;

}