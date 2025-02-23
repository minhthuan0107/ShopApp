package com.project.shopapp.components;

import com.project.shopapp.configurations.UserDetailsImpl;
import com.project.shopapp.exception.InvalidParamException;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class JwtTokenUtils {
    @Value("${bezkoder.jwtSecret}")
    private String jwtSecret;//save to environment
    @Value("${bezkoder.jwtExpirationMs}")
    private int jwtExpirationMs;
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtils.class);


    public String generateJwtToken(Authentication authentication) throws Exception {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        Date dateJwtDate = new Date();
        try {
            List<String> roles = userPrincipal.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            String token = Jwts.builder()
                    .setSubject(userPrincipal.getPhoneNumber())
                    .claim("roles", roles)
                    .setIssuedAt(dateJwtDate)
                    .setExpiration(new Date(dateJwtDate.getTime() + jwtExpirationMs))
                    .signWith(SignatureAlgorithm.HS256, jwtSecret)
                    .compact();
            return token;
        } catch (Exception e) {
            throw new InvalidParamException("Cannot create JWT token, error: " + e.getMessage());
        }
    }
    public String getphoneNumberFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is usnupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
