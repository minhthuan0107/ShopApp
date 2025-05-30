package com.project.shopapp.responses.coupon;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.BaseEntity;
import com.project.shopapp.models.Coupon;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
public class CouponResponse extends BaseEntity {
    private Long id;
    private String code;
    private String type;
    private BigDecimal value;
    @JsonProperty(value = "min_order_value")
    private BigDecimal minOrderValue;
    @JsonProperty(value = "expiry_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
    @JsonProperty(value = "is_active")
    private boolean isActive;

    public static CouponResponse fromCoupon(Coupon coupon) {
        CouponResponse couponResponse = CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .type(coupon.getType())
                .value(coupon.getValue())
                .minOrderValue(coupon.getMinOrderValue())
                .expiryDate(coupon.getExpiryDate())
                .isActive(coupon.isActive())
                .build();
        couponResponse.setCreateAt(coupon.getCreateAt());
        couponResponse.setUpdateAt(coupon.getUpdateAt());
        return couponResponse;
    }
}
