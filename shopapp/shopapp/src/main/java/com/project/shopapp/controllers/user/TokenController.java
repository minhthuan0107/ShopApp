package com.project.shopapp.controllers.user;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.response.ResponseObject;
import com.project.shopapp.services.token.TokenService;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("${api.prefix}/tokens")
public class TokenController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private LocalizationUtils localizationUtils;

    @PostMapping("/newAccessToken")
    public ResponseEntity<ResponseObject> generateAccessTokenFromRefreshToken( @RequestParam String refreshToken)  {
        try {
            String newAccessToken = tokenService.generateAccessTokenFromRefreshToken(refreshToken);
            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseObject.builder()
                    .status(HttpStatus.CREATED)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.CREATED_ACCESS_TOKEN_SUCCESSFULLY))
                    .data(Collections.singletonMap("access_token", newAccessToken))
                    .build());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseObject.builder()
                    .status(HttpStatus.UNAUTHORIZED)
                    .message(e.getMessage())
                    .build());
        }
    }
    }
