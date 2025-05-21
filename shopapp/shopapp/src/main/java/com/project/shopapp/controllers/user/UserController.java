package com.project.shopapp.controllers.user;

import com.project.shopapp.components.JwtTokenUtils;
import com.project.shopapp.dtos.user.*;
import com.project.shopapp.models.User;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.responses.user.SigninResponse;
import com.project.shopapp.responses.user.UserResponse;
import com.project.shopapp.services.user.UserService;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @PostMapping("/signup")
    public ResponseEntity<?> singnup(@Valid @RequestBody UserDto userDto,
                                     BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .data(null)
                        .message(errorMessages.toString())
                        .build());
            }
            User user = userService.createUser(userDto);
            return ResponseEntity.status(HttpStatus.CREATED).
                    body(ResponseObject.builder()
                            .data(UserResponse.fromUser(user))
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.REGISTRATION_SUCCESSFULLY))
                            .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.REGISTRATION_FAILED, e.getMessage()))
                    .build());
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<SigninResponse> signin(@Valid @RequestBody UserLoginDto userLoginDto) {
        try {
            SigninDto signinDto = userService.signin(userLoginDto.getPhoneNumber(), userLoginDto.getPassword());
            return ResponseEntity.ok(SigninResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                    .accessToken(signinDto.getAccessToken())
                    .refreshToken(signinDto.getRefreshToken())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    SigninResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_FAILED, e.getMessage()))
                            .build()
            );
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseObject> getCurrentUser(
            HttpServletRequest request) throws Exception {
        String phoneNumber = (String) request.getAttribute("phoneNumber");
        // Tìm user trong DB theo phoneNumber từ token
        User user = userService.getUserByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(UserResponse.fromUser(user))
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_FOUND_SUCCESS))
                .build());
    }

    @PatchMapping("/change-password")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ResponseObject> changePassword(
            @RequestBody ChangePasswordDto changePasswordDto,
            HttpServletRequest httpRequest,
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
            String phoneNumber = (String) httpRequest.getAttribute("phoneNumber");
            userService.changePassword(phoneNumber, changePasswordDto);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_CHANGE_SUCCESSFULLY))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PatchMapping("/update-profile")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ResponseObject> updateProfile(
            @RequestBody UpdateProfileDto updateProfileDto,
            HttpServletRequest httpRequest,
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
            String phoneNumber = (String) httpRequest.getAttribute("phoneNumber");
            UserResponse updateUser = userService.updateProfile(phoneNumber, updateProfileDto);
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
}
