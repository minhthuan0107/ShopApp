package com.project.shopapp.services.token;

import com.project.shopapp.components.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService implements ITokenService {
    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Override
    public String generateAccessTokenFromRefreshToken(String refreshToken) throws Exception {
        // Không cần kiểm tra lại token ở đây vì đã được xác minh trong bộ lọc
        jwtTokenUtils.validateRefreshToken(refreshToken);
        String newAccessToken = jwtTokenUtils.generateAccessTokenFromRefreshToken(refreshToken);
        return newAccessToken;
    }
}
