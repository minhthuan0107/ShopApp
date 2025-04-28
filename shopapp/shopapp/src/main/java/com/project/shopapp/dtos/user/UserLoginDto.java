package com.project.shopapp.dtos.user;

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
    @NotBlank(message = "Phone number is requied")
    private String phoneNumber;
    @NotBlank(message = "Password cannot be blank")
    private String password;


}
