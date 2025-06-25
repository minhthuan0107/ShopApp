package com.project.shopapp.dtos.customer.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("phone_number")
    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(min = 9, message = "Số điện thoại phải có ít nhất 9 chữ số")
    @Pattern(regexp = "\\d+", message = "Số điện thoại chỉ được chứa các chữ số")
    private String phoneNumber;
    private String address;
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
    @JsonProperty("retype_password")
    private String retypePassword;
    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;
    @JsonProperty("facebook_account_id")
    private String facebookAccountId;
    @JsonProperty("google_account_id")
    private String googleAccountId;


}
