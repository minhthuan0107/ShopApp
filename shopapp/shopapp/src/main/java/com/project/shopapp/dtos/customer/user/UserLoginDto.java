package com.project.shopapp.dtos.customer.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDto {
    @JsonProperty("phone_number")
    @NotBlank(message = "Số điện thoại đăng nhập không được để trống")
    private String phoneNumber;
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
}
