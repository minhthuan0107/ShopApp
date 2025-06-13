package com.project.shopapp.services.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;


import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Service
public class AuthService implements IAuthService{
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
    private String googleUserInfoUri;


    public String generateAuthUrl(String loginType) {
        String url = "";
        loginType = loginType.trim().toLowerCase();
        if ("google".equals(loginType)) {
            // GoogleAuthorizationCodeRequestUrl = Tạo URL đăng nhập Google
            //Mục tiêu = Lấy code để sau đó backend dùng mã đó lấy access token
            GoogleAuthorizationCodeRequestUrl urlBuilder = new GoogleAuthorizationCodeRequestUrl(
                    googleClientId,
                    googleRedirectUri,
                    Arrays.asList("email", "profile", "openid"));
            url = urlBuilder.build();
        }
        return url;
    }
    public Map<String, Object> authenticateAndFetchProfile(String code, String loginType) throws IOException {
        String accessToken;

        switch (loginType.toLowerCase()) {
            case "google":
                // 1. Lấy access token từ Google
                accessToken = new GoogleAuthorizationCodeTokenRequest(
                        new NetHttpTransport(), new GsonFactory(),
                        googleClientId,
                        googleClientSecret,
                        code,
                        googleRedirectUri
                ).execute().getAccessToken();

                // 2. Tạo WebClient với header Authorization: Bearer <access_token>
                WebClient webClient = WebClient.builder()
                        .baseUrl(googleUserInfoUri)
                        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .build();

                // 3. Gửi GET request đến endpoint lấy thông tin người dùng
                String responseBody = webClient.get()
                        .retrieve()
                        .bodyToMono(String.class)
                        .block(); // blocking vì đang trong hàm đồng bộ

                // 4. Parse JSON response sang Map
                return new ObjectMapper().readValue(responseBody, new TypeReference<>() {});

            default:
                System.out.println("Unsupported login type: " + loginType);
                return null;
        }
    }
}
