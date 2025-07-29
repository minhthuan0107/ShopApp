package com.project.shopapp.controllers.admin;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.admin.auth.LoginAdminDto;
import com.project.shopapp.dtos.admin.auth.SigninAdminDto;
import com.project.shopapp.responses.customer.user.SigninResponse;
import com.project.shopapp.services.admin.auth.AuthAdminService;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.admin-prefix}/auth")
public class AdminAuthController {
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private AuthAdminService authAdminService;

    @PostMapping("/signin")
    public ResponseEntity<SigninResponse> singinAdmin(@Valid @RequestBody LoginAdminDto loginAdminDto) {
        try {
            SigninAdminDto signinAdminDto = authAdminService.signinAdmin(loginAdminDto.getPhoneNumber(), loginAdminDto.getPassword());
            return ResponseEntity.ok(SigninResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                    .accessToken(signinAdminDto.getAccessToken())
                    .refreshToken(signinAdminDto.getRefreshToken())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    SigninResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_FAILED, e.getMessage()))
                            .build()
            );
        }
    }
}
