package com.project.shopapp.services.coupon;

import com.project.shopapp.commons.CouponStatus;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.coupon.CouponDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Coupon;
import com.project.shopapp.models.User;
import com.project.shopapp.models.UserCoupon;
import com.project.shopapp.repositories.CouponRepository;
import com.project.shopapp.repositories.UserCouponRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.responses.coupon.ApplyCouponResponse;
import com.project.shopapp.responses.coupon.CouponResponse;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CouponService implements ICouponService {
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserCouponRepository userCouponRepository;

    @Override
    @Transactional
    public CouponResponse createCoupon(CouponDto couponDto) throws Exception {
        if (couponRepository.existsByCode(couponDto.getCode())) {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.COUPON_CODE_ALREADY_EXISTS));
        }
        Coupon coupon = Coupon.builder()
                .type(couponDto.getType())
                .code(couponDto.getCode())
                .value(couponDto.getValue())
                .minOrderValue(couponDto.getMinOrderValue())
                .expiryDate(couponDto.getExpiryDate())
                .build();
        coupon.setActive(true);
        couponRepository.save(coupon);
        return CouponResponse.fromCoupon(coupon);
    }

    @Override
    @Transactional
    public CouponResponse checkCoupon(Long userId, String code) throws Exception {
        User existUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND)));
        Coupon coupon = couponRepository.findByCode(code).
                orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.COUPON_NOT_FOUND)));
        if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(LocalDate.now())) {
            coupon.setActive(false);
            couponRepository.save(coupon);
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.COUPON_EXPIRED));
        }
        // Kiểm tra coupon đã được dùng bởi user này chưa
        boolean alreadyUsed = userCouponRepository.existsByUserIdAndCouponIdAndStatus(
                userId, coupon.getId(), CouponStatus.USED);

        if (alreadyUsed) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.COUPON_ALREADY_USED));
        }
        return CouponResponse.fromCoupon(coupon);
    }
}
