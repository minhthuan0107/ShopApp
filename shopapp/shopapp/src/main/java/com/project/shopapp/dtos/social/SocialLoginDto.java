package com.project.shopapp.dtos.social;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialLoginDto extends SocialAccountDto {
    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("email")
    private String email;

    @JsonProperty("fullname")
    private String fullname;

    @JsonProperty("profile_image")
    private String profileImage;

    // Password may not be needed for social login but required for traditional login
    @NotBlank(message = "Password cannot be blank")
    private String password;
    @Min(value = 1, message = "Vui lòng nhập ID của vai trò")
    @JsonProperty("role_id")
    private Long roleId;
}
