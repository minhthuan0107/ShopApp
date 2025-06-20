package com.project.shopapp.services.customer.token;

import com.project.shopapp.components.JwtTokenUtils;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Token;
import com.project.shopapp.repositories.TokenRepository;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService implements ITokenService {
    @Autowired
    private JwtTokenUtils jwtTokenUtils;
    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private LocalizationUtils localizationUtils;

    @Override
    public String generateAccessTokenFromRefreshToken(String refreshToken) throws Exception {
        // Kiểm tra tính hợp lệ của refresh token (đã được xác minh trong bộ lọc)
        jwtTokenUtils.validateRefreshToken(refreshToken);

        // Tìm refresh token trong cơ sở dữ liệu và kiểm tra trạng thái 'revoked'
        Token token = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(
                        MessageKeys.REFRESH_TOKEN_NOT_FOUND)));
        if (token.isRevoked()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(
                    MessageKeys.REFRESH_TOKEN_REVOKED));
        }

        // Tạo access token mới từ refresh token hợp lệ
        String newAccessToken = jwtTokenUtils.generateAccessTokenFromRefreshToken(refreshToken);
        return newAccessToken;
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String refreshToken) throws Exception {
        jwtTokenUtils.validateRefreshToken(refreshToken);
        Token token = tokenRepository.findByToken(refreshToken).
                orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(
                        MessageKeys.REFRESH_TOKEN_NOT_FOUND)));
        if (token.isRevoked()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(
                    MessageKeys.REFRESH_TOKEN_REVOKED));
        }
        token.setRevoked(true);
        tokenRepository.save(token);
    }
}
