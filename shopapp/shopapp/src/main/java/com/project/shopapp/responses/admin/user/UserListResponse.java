package com.project.shopapp.responses.admin.user;

import com.project.shopapp.responses.customer.user.UserResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Builder
@Data
public class UserListResponse {

    private List<UserResponse> userResponses;
    private int totalPages;
    private long totalItems;
    private int currentPage;
}
