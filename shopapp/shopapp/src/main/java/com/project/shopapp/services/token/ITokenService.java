package com.project.shopapp.services.token;

public interface ITokenService{
    String  generateAccessTokenFromRefreshToken (String refreshToken) throws Exception;
}
