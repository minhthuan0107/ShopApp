package com.project.shopapp.responses.coupon;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.commons.CouponStatus;
import com.project.shopapp.models.UserCoupon;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
public class ApplyCouponResponse {
    private Long id;
    private String code;
    private String type;
    @JsonProperty(value = "user_name")
    private String userName;
    private BigDecimal value;
    private CouponStatus status;
    @JsonProperty(value = "applied_at")
    private LocalDate appliedAt;
    public static ApplyCouponResponse fromUserCoupon (UserCoupon userCoupon){
        ApplyCouponResponse applyCouponResponse = ApplyCouponResponse.builder()
                .id(userCoupon.getId())
                .code(userCoupon.getCoupon().getCode())
                .type(userCoupon.getCoupon().getType())
                .userName(userCoupon.getUser().getFullname())
                .value(userCoupon.getCoupon().getValue())
                .status(userCoupon.getStatus())
                .appliedAt(userCoupon.getAppliedAt())
                .build();
        return applyCouponResponse;
    }



}
