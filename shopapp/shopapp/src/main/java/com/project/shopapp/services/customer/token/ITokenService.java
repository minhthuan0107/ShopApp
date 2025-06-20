package com.project.shopapp.services.customer.token;

public interface ITokenService{
    String  generateAccessTokenFromRefreshToken (String refreshToken) throws Exception;

    void revokeRefreshToken (String  refreshToken) throws Exception;
}
