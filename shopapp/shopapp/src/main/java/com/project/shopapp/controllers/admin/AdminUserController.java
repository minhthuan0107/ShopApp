package com.project.shopapp.controllers.admin;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.configurations.UserDetailsImpl;
import com.project.shopapp.dtos.customer.user.UpdateProfileDto;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.responses.admin.user.UserListResponse;
import com.project.shopapp.responses.user.UserResponse;
import com.project.shopapp.services.admin.user.AdminUserService;
import com.project.shopapp.ultis.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.admin-prefix}/users")
public class AdminUserController {
    @Autowired
    private AdminUserService userService;
    @Autowired
    private LocalizationUtils localizationUtils;

    @GetMapping("/get-all")
    public ResponseEntity<UserListResponse> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(required = false, defaultValue = "") String keyword) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<UserResponse> userPage = userService.getAllUsers(pageRequest, keyword);
        //Lấy tổng số trang
        int totalPages = userPage.getTotalPages();
        //Lấy tổng số khách hàng
        long totalItems = userPage.getTotalElements();
        //Lấy
        List<UserResponse> userResponseList = userPage.getContent();
        return ResponseEntity.ok(UserListResponse.builder()
                .userResponses(userResponseList)
                .totalPages(totalPages)
                .totalItems(totalItems)
                .currentPage(page + 1)
                .build());
    }

    @PatchMapping("/update-profile/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> updateProfileUserByAdmin(
            @RequestBody UpdateProfileDto updateProfileDto,
            @PathVariable Long userId,
            BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message(errorMessages.toString())
                        .build());
            }
            UserResponse updateUser = userService.updateProfileByAdmin(userId, updateProfileDto);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_UPDATE_PROFILE_SUCCESSFULLY))
                    .data(updateUser)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PatchMapping("/toggle-status/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> toggleUserStatus(@PathVariable Long userId) {
        try {
            UserResponse user = userService.toggleUserStatus(userId);
            String message = user.isActive()
                    ? "Tài khoản đã được mở khóa thành công"
                    : "Tài khoản đã bị khóa thành công";
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message(message)
                    .data(user)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }
}
