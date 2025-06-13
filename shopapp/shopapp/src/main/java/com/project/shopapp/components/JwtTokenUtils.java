package com.project.shopapp.components;

import com.project.shopapp.configurations.UserDetailsImpl;
import com.project.shopapp.configurations.UserDetailsServiceImpl;
import com.project.shopapp.exception.InvalidParamException;
import com.project.shopapp.services.user.UserService;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class JwtTokenUtils {
    @Value("${bezkoder.jwtSecret}")
    private String jwtSecret;//save to environmen
    @Value("${bezkoder.jwtExpirationMs}")
    private int jwtExpirationMs;
    @Value("${bezkoder.refreshTokenSecret}")
    private String refreshTokenSecret;
    @Value("${bezkoder.refreshTokenExpirationMs}")
    private int refreshTokenExpirationMs;
    @Autowired
    private UserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtils.class);


    public String generateAccessToken(Authentication authentication) throws Exception {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        //Nếu đăng nhập bình thường thì set subject là phonenumber, còn gooler là GoogleAccountId
        String subject = userPrincipal.getPhoneNumber() != null ? userPrincipal.getPhoneNumber()
                : userPrincipal.getGoogleAccountId();
        //Nếu có phoneNumber là đăng nhập bình thường, còn không có thì đăng nhập gg
        String loginType = userPrincipal.getPhoneNumber() != null ? "Phone" : "Google";
        Date dateJwtDate = new Date();
        try {
            List<String> roles = userPrincipal.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            String token = Jwts.builder()
                    .setSubject(subject)
                    .claim("userId", userPrincipal.getId())
                    .claim("loginType", loginType)
                    .claim("authorities", roles)
                    .setIssuedAt(dateJwtDate)
                    .setExpiration(new Date(dateJwtDate.getTime() + jwtExpirationMs))
                    .signWith(SignatureAlgorithm.HS256, jwtSecret)
                    .compact();
            return token;
        } catch (Exception e) {
            throw new InvalidParamException("Không thể tạo Access Token ,lỗi: " + e.getMessage());
        }
    }

    public String generateRefreshToken(Authentication authentication) throws Exception {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        String subject = userPrincipal.getPhoneNumber() != null ? userPrincipal.getPhoneNumber()
                : userPrincipal.getGoogleAccountId();
        //Nếu có phoneNumber là đăng nhập bình thường, còn không có thì đăng nhập gg
        String loginType = userPrincipal.getPhoneNumber() != null ? "Phone" : "Google";

        Date now = new Date();
        try {
            String refreshToken = Jwts.builder()
                    .setSubject(subject)
                    .claim("loginType", loginType)
                    .setIssuedAt(now)
                    .setExpiration(new Date(now.getTime() + refreshTokenExpirationMs)) // refresh token sống lâu hơn
                    .signWith(SignatureAlgorithm.HS256, refreshTokenSecret)
                    .compact();
            return refreshToken;
        } catch (Exception e) {
            throw new InvalidParamException("Không thể tạo Refresh Token, lỗi: " + e.getMessage());
        }
    }

    public String getSubjectFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public String getLoginTypeFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return claims.get("loginType", String.class);
    }

    public String generateAccessTokenFromRefreshToken(String refreshToken) throws Exception {
        try {
            // Kiểm tra tính hợp lệ của refresh token
            Claims claims = Jwts.parser()
                    .setSigningKey(refreshTokenSecret)
                    .parseClaimsJws(refreshToken)
                    .getBody();
            String subject = claims.getSubject(); // subject là phoneNumber hoặc googleAccountId
            String loginType = claims.get("loginType", String.class);
            //Kiểm tra xem có thông tin đăng nhập hay chưa
            if (subject == null || subject.isEmpty()) {
                throw new InvalidParamException("Thông tin đăng nhập không có trong refresh token");
            }
            UserDetailsImpl userDetails;

            if ("Phone".equalsIgnoreCase(loginType)) {
                userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(subject);
            } else if ("Google".equalsIgnoreCase(loginType)) {
                userDetails = (UserDetailsImpl) ((UserDetailsServiceImpl) userDetailsService).loadUserByGoogleAccountId(subject);
            } else {
                throw new InvalidParamException("Loại đăng nhập không hợp lệ trong Refresh Token");
            }
           // Thiết lập thông tin xác thực
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            // Sử dụng hàm generateAccessToken đã có để tạo access token mới
            return generateAccessToken(authentication);
        } catch (Exception e) {
            throw new InvalidParamException("Không thể tạo access token từ refresh token: " + e.getMessage());
        }
    }

    public boolean validateJwtToken(String authToken) throws InvalidParamException {
        if (authToken == null || authToken.trim().isEmpty()) {
            // Ném exception nếu token là null hoặc rỗng
            throw new InvalidParamException("Token không được phép là null hoặc rỗng.");
        }
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Chữ ký JWT không hợp lệ: {}", e.getMessage());
            throw new InvalidParamException("Chữ ký JWT không hợp lệ: " + e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Token JWT không hợp lệ: {}", e.getMessage());
            throw new InvalidParamException("Token JWT không hợp lệ: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT đã hết hạn: {}", e.getMessage());
            throw new InvalidParamException("Token JWT đã hết hạn: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT không được hỗ trợ: {}", e.getMessage());
            throw new InvalidParamException("Token JWT không được hỗ trợ: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Chuỗi claim của JWT trống: {}", e.getMessage());
            throw new InvalidParamException("Chuỗi claim của JWT trống: " + e.getMessage());
        }
    }

    public boolean validateRefreshToken(String authToken) throws InvalidParamException {
        if (authToken == null || authToken.trim().isEmpty()) {
            // Ném exception nếu token là null hoặc rỗng
            throw new InvalidParamException("Token không được phép là null hoặc rỗng.");
        }
        try {
            Jwts.parser().setSigningKey(refreshTokenSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Chữ ký JWT không hợp lệ: {}", e.getMessage());
            throw new InvalidParamException("Chữ ký JWT không hợp lệ: " + e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Token JWT không hợp lệ: {}", e.getMessage());
            throw new InvalidParamException("Token JWT không hợp lệ: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT đã hết hạn: {}", e.getMessage());
            throw new InvalidParamException("Token JWT đã hết hạn: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT không được hỗ trợ: {}", e.getMessage());
            throw new InvalidParamException("Token JWT không được hỗ trợ: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Chuỗi claim của JWT trống: {}", e.getMessage());
            throw new InvalidParamException("Chuỗi claim của JWT trống: " + e.getMessage());
        }
    }
}
