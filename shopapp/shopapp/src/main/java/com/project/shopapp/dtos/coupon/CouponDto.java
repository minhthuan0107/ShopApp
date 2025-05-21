package com.project.shopapp.dtos.coupon;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponDto {

    private String type; // "fixed" hoặc "percent"

    private BigDecimal value; // Giá trị giảm: 50000 hoặc 10 (%)

    private BigDecimal minOrderValue; // Đơn hàng tối thiểu để áp dụng coupon

    private LocalDate expiryDate; // Ngày hết hạn

    private boolean isActive; // Trạng thái hoạt động

}
