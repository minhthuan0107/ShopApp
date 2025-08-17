package com.project.shopapp.repositories;

import com.project.shopapp.commons.CouponStatus;
import com.project.shopapp.models.Coupon;
import com.project.shopapp.models.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon,Long> {
    boolean existsByUserIdAndCouponIdAndStatus (Long userId, Long couponId, CouponStatus status);

    Optional<UserCoupon> findByOrderId (Long orderId);

    Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId);
}
