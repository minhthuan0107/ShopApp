package com.project.shopapp.services.admin.user;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.customer.user.UpdateProfileDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.responses.user.UserResponse;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class AdminUserService implements IAdminUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LocalizationUtils localizationUtils;

    @Override
    public Page<UserResponse> getAllUsers(PageRequest pageRequest, String keyword) {
        Page<User> userPage;
        if (keyword == null || keyword.trim().isEmpty()) {
            //Nếu keyword rỗng thì lấy danh sách
            userPage = userRepository.findAllExcludeAdmin(pageRequest);
        } else {
            //Nếu có keyword thì tìm theo keyword
            userPage = userRepository.searchUsersByKeyword(keyword, pageRequest);
        }
        return userPage.map(user -> UserResponse.fromUser(user));
    }


    @Transactional
    @Override
    public UserResponse updateProfileByAdmin(Long userId, UpdateProfileDto updateProfileDto) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND))
        );
        if (updateProfileDto.getFullName() != null) {
            user.setFullname(updateProfileDto.getFullName());
        }
        if (updateProfileDto.getAddress() != null) {
            user.setAddress(updateProfileDto.getAddress());
        }
        if (updateProfileDto.getDateOfBirth() != null) {
            user.setDateOfBirth(updateProfileDto.getDateOfBirth());
        }
        User updateUser = userRepository.save(user);
        return UserResponse.fromUser(updateUser);
    }

    @Override
    @Transactional
    public UserResponse toggleUserStatus(Long userId) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND))
        );
        boolean newStatus = !user.isActive();
        user.setActive(newStatus);
        userRepository.save(user);
        return UserResponse.fromUser(user);
    }
}
