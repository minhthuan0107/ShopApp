package com.project.shopapp.controllers.user;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.responses.object.ResponseObject;
import com.project.shopapp.services.customer.token.TokenService;
import com.project.shopapp.ultis.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("${api.prefix}/tokens")
public class TokenController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private LocalizationUtils localizationUtils;

    @PostMapping("/new-access-token")
    public ResponseEntity<ResponseObject> generateAccessTokenFromRefreshToken(@RequestParam String refreshToken) {
        try {
            String newAccessToken = tokenService.generateAccessTokenFromRefreshToken(refreshToken);
            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseObject.builder()
                    .status(HttpStatus.CREATED)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.CREATED_ACCESS_TOKEN_SUCCESSFULLY))
                    .data(Collections.singletonMap("access_token", newAccessToken))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseObject.builder()
                    .status(HttpStatus.UNAUTHORIZED)
                    .message(e.getMessage())
                    .build());
        }
    }
    @PatchMapping("/refresh-token")
    public ResponseEntity<ResponseObject> revokeRefreshToken(@RequestParam String refreshToken) {
        try {
            tokenService.revokeRefreshToken(refreshToken);
            return  ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.REFRESH_TOKEN_REVOKED_SUCCESSFULLY))
                    .build());
        } catch (Exception e){
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }
}
