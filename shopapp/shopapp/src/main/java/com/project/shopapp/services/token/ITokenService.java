package com.project.shopapp.services.token;

import com.project.shopapp.models.Token;

import java.util.Optional;

public interface ITokenService{
    String  generateAccessTokenFromRefreshToken (String refreshToken) throws Exception;

    void revokeRefreshToken (String  refreshToken) throws Exception;
}
