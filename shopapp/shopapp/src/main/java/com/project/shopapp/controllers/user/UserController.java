package com.project.shopapp.controllers.user;
import com.project.shopapp.configurations.UserDetailsImpl;
import com.project.shopapp.dtos.customer.social.SocialLoginDto;
import com.project.shopapp.dtos.customer.user.*;
import com.project.shopapp.models.User;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.responses.user.SigninResponse;
import com.project.shopapp.responses.user.UserResponse;
import com.project.shopapp.services.customer.auth.AuthService;
import com.project.shopapp.services.customer.user.UserService;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("${api.prefix}/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private AuthService authService;

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
    public ResponseEntity<ResponseObject> getCurrentUser(Authentication authentication) throws Exception {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();

        User user = userService.getUserById(userId);

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
            Authentication authentication,
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
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();
            userService.changePassword(userId, changePasswordDto);
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
            Authentication authentication,
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
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();
            UserResponse updateUser = userService.updateProfile(userId, updateProfileDto);
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

    //Angular, bấm đăng nhập gg, redirect đến trang đăng nhập google, đăng nhập xong có "code"
    //Từ "code" => google token => lấy ra các thông tin khác
    @GetMapping("/auth/social-login")
    public ResponseEntity<ResponseObject> socialAuth(@RequestParam("login_type") String loginType) {
        //request.getRequestURI()
        loginType = loginType.trim().toLowerCase();  // Loại bỏ dấu cách và chuyển thành chữ thường
        String url = authService.generateAuthUrl(loginType);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.SOCIAL_LOGIN_URL_GENERATED))
                .data(url)
                .build());
    }

    @GetMapping("/auth/social/callback")
    public ResponseEntity<SigninResponse> callback(
            @RequestParam("code") String code,
            @RequestParam("login_type") String loginType

    ) throws Exception {
        // Call the AuthService to get user info
        Map<String, Object> userInfo = authService.authenticateAndFetchProfile(code, loginType);
        if (userInfo == null || userInfo.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(SigninResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_FAILED, "Không lấy được thông tin từ social"))
                            .build());
        }
        // Extract user information from userInfo map
        String accountId = "";
        String name = "";
        String picture = "";
        String email = "";

        if (loginType.trim().equals("google")) {
            accountId = (String) Objects.requireNonNullElse(userInfo.get("sub"), "");
            name = (String) Objects.requireNonNullElse(userInfo.get("name"), "");
            picture = (String) Objects.requireNonNullElse(userInfo.get("picture"), "");
            email = (String) Objects.requireNonNullElse(userInfo.get("email"), "");
        } else if (loginType.trim().equals("facebook")) {
            accountId = (String) Objects.requireNonNullElse(userInfo.get("id"), "");
            name = (String) Objects.requireNonNullElse(userInfo.get("name"), "");
            email = (String) Objects.requireNonNullElse(userInfo.get("email"), "");
            // Lấy URL ảnh từ cấu trúc dữ liệu của Facebook
            Object pictureObj = userInfo.get("picture");
            if (pictureObj instanceof Map) {
                Map<?, ?> pictureData = (Map<?, ?>) pictureObj;
                Object dataObj = pictureData.get("data");
                if (dataObj instanceof Map) {
                    Map<?, ?> dataMap = (Map<?, ?>) dataObj;
                    Object urlObj = dataMap.get("url");
                    if (urlObj instanceof String) {
                        picture = (String) urlObj;
                    }
                }
            }
        }
        // Tạo đối tượng UserLoginDTO
        SocialLoginDto socialLoginDto = SocialLoginDto.builder()
                .email(email)
                .fullname(name)
                .password("")
                .phoneNumber("")
                .profileImage(picture)
                .build();

        if (loginType.trim().equals("google")) {
            socialLoginDto.setGoogleAccountId(accountId);
        } else if (loginType.trim().equals("facebook")) {
            socialLoginDto.setFacebookAccountId(accountId);
        }
        SigninDto signinDto = userService.signinSocial(socialLoginDto);
        return ResponseEntity.ok(SigninResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                .accessToken(signinDto.getAccessToken())
                .refreshToken(signinDto.getRefreshToken())
                .build());

    }
}
