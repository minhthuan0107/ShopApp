package com.project.shopapp.responses.user;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.Role;
import com.project.shopapp.models.User;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("address")
    private String address;
    @JsonProperty("is_active")
    private boolean active;
    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;
    @JsonProperty("create_at")
    private LocalDateTime createAt;
    @JsonProperty("facebook_account_id")
    private String facebookAccountId;
    @JsonProperty("google_account_id")
    private String googleAccountId;
    @JsonProperty("role")
    private Role role;
    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullname())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .active(user.isActive())
                .dateOfBirth(user.getDateOfBirth())
                .createAt(user.getCreateAt())
                .facebookAccountId(user.getFacebookAccountId())
                .googleAccountId(user.getGoogleAccountId())
                .role(user.getRole())
                .build();
    }
}
