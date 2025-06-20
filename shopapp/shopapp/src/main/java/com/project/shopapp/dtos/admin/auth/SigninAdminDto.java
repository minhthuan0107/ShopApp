package com.project.shopapp.dtos.admin.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SigninAdminDto {
    private String accessToken;
    private String refreshToken;
}
