package com.project.shopapp.dtos.customer.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SigninDto {
    private String accessToken;
    private String refreshToken;
}


