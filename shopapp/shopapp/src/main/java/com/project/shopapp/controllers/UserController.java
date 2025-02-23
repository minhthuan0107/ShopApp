package com.project.shopapp.controllers;

import com.project.shopapp.components.JwtTokenUtils;
import com.project.shopapp.configurations.UserDetailsImpl;
import com.project.shopapp.dtos.UserDto;
import com.project.shopapp.dtos.UserLoginDto;
import com.project.shopapp.models.User;
import com.project.shopapp.response.ResponseObject;
import com.project.shopapp.response.user.SigninResponse;
import com.project.shopapp.response.user.UserResponse;
import com.project.shopapp.services.user.UserService;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.ultis.MessageKeys;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.CREATED)
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
            String token = userService.signin(userLoginDto.getPhoneNumber(), userLoginDto.getPassword());
            return ResponseEntity.ok(SigninResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                    .token(token)
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
}
