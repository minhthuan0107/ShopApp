package com.project.shopapp.dtos.customer.order;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.dtos.customer.orderdetail.OrderDetailDto;
import com.project.shopapp.dtos.customer.payment.PaymentDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

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
    @NotBlank(message = "Vui lòng nhập địa chỉ chi tiết (số nhà, thôn, xóm,...)")
    @JsonProperty("address_detail")
    private String addressDetail;
    private String note;
    @JsonProperty("order_details")
    private List<OrderDetailDto> orderDetails;
    @JsonProperty("payment")
    private PaymentDto payment;
    @JsonProperty("is_buy_now")
    private boolean isBuyNow;
    @JsonProperty("coupon_code")
    private String couponCode;
    @JsonProperty("province")
    private String province;
    @JsonProperty("district")
    private String district;
    @JsonProperty("ward")
    private String ward;
    @JsonProperty("shipping_method")
    private String shippingMethod;
}