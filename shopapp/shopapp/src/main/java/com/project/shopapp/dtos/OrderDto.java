package com.project.shopapp.dtos;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;


@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    @JsonProperty("user_id")
    @Min(value = 1,message = "User's ID must be >0")
    private Long userId;
    @JsonProperty("fullname")
    private String fullName;
    private String email;
    @JsonProperty("phone_number")
    @Size(min = 5,message = "Phone number must be at least 5 characters")
    @NotBlank(message = "Phone number is requied")
    private String phoneNumber;
    private String note;
    @JsonProperty("total_money")
    @Min(value = 0,message = "Total money must be >=0")
    private Float totalMoney;
    @JsonProperty("shipping_method")
    private String shippingMethod;
    @JsonProperty("shipping_address")
    private String shippingAddress;
    @JsonProperty("shipping_date")
    private LocalDate shippingDate;
    @JsonProperty("tracking_number")
    private String trackingNumber;
    @JsonProperty("payment_method")
    private String paymentMethod;


}
