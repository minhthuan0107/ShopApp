package com.project.shopapp.dtos.coupon;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponDto {
    @NotBlank(message = "Loại coupon không được để trống")
    @Pattern(regexp = "fixed|percent", message = "Loại coupon phải là 'fixed' hoặc 'percent'")
    private String type;

    @NotBlank(message = "Mã coupon không được để trống")
    private String code;

    @NotNull(message = "Giá trị giảm không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá trị giảm phải lớn hơn 0")
    private BigDecimal value; // Giá trị giảm: 50000 hoặc 10 (%)

    @NotNull(message = "Giá trị đơn hàng tối thiểu không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Đơn hàng tối thiểu phải lớn hơn 0")
    @JsonProperty(value = "min_order_value")
    private BigDecimal minOrderValue; // Đơn hàng tối thiểu để áp dụng coupon

    @NotNull(message = "Ngày hết hạn không được để trống")
    @Future(message = "Ngày hết hạn phải là một thời điểm trong tương lai")
    @JsonProperty(value = "expiry_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

}
