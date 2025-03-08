package com.project.shopapp.dtos;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;


@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    @JsonProperty("full_name")
    private String fullName;
    private String email;
    @JsonProperty("phone_number")
    @Size(min = 9, message = "Số điện thoại phải có ít nhất 9 ký tự")
    @NotBlank(message = "Số điện thoại là bắt buộc")
    private String phoneNumber;
    @NotBlank(message = "Địa chỉ là bắt buộc")
    private String address;
    private String note;
    @JsonProperty("total_price")
    @Min(value = 0, message = "Tổng số tiền phải lớn hơn 0")
    private BigDecimal totalPrice;
    @JsonProperty("order_details")
    private List<OrderDetailDto> orderDetails;
    @JsonProperty("payment")
    private PaymentDto payment;
}