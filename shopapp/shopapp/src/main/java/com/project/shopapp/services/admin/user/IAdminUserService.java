package com.project.shopapp.services.admin.user;

import com.project.shopapp.dtos.customer.user.UpdateProfileDto;
import com.project.shopapp.responses.customer.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IAdminUserService {
    Page<UserResponse> getAllUsers(PageRequest pageRequest, String keyword);

    UserResponse updateProfileByAdmin (Long userId, UpdateProfileDto updateProfileDto) throws Exception;

    UserResponse toggleUserStatus(Long userId) throws Exception;
}
