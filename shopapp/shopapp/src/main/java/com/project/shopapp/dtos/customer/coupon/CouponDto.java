package com.project.shopapp.dtos.customer.coupon;

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
    private BigDecimal value;

    @DecimalMin(value = "0.0", inclusive = true, message = "Giá trị đơn hàng tối thiểu phải lớn hơn hoặc bằng 0")
    @JsonProperty("min_order_value")
    private BigDecimal minOrderValue = BigDecimal.ZERO;

    @Min(value = 1, message = "Số lượng mã giảm giá phải lớn hơn hoặc bằng 1")
    @NotNull(message = "Số lượng mã giảm giá không được để trống")
    private Integer quantity;

    @NotNull(message = "Ngày hết hạn không được để trống")
    @Future(message = "Ngày hết hạn phải là một thời điểm trong tương lai")
    @JsonProperty("expiry_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

}
