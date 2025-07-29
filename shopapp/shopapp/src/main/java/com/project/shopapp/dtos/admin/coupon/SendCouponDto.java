package com.project.shopapp.dtos.admin.coupon;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendCouponDto {
    @NotBlank(message = "Mã giảm giá không được để trống")
    private String couponCode;

    @NotEmpty(message = "Danh sách người dùng không được rỗng")
    private List<Long> userIds;
}
