package com.project.shopapp.services.admin.auth;

import com.project.shopapp.components.JwtTokenUtils;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.admin.auth.SigninAdminDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Token;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.TokenRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthAdminService implements IAuthAdminService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenRepository tokenRepository;
    @Override
    @Transactional
    public SigninAdminDto signinAdmin(String phoneNumber, String password) throws Exception {
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.WRONG_PHONE_NUMBER));
        }
        User existingUser = optionalUser.get();
        if (!existingUser.getRole().isAdmin()) {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.ACCESS_DENIED));
        }
        if (!passwordEncoder.matches(password, existingUser.getPassword())) {
            throw new BadCredentialsException(
                    localizationUtils.getLocalizedMessage(MessageKeys.WRONG_PASSWORD));
        }
        // Tạo authenticationToken với tên đăng nhập và mật khẩu
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(phoneNumber, password);
        // Gọi authenticate để thực hiện xác thực
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        // Nếu xác thực thành công, trả về token
        String accessToken = jwtTokenUtils.generateAccessToken(authentication);
        String refreshToken = jwtTokenUtils.generateRefreshToken(authentication);

        Token tokenEntity = new Token();
        tokenEntity.setToken(refreshToken); // Lưu refreshToken
        tokenEntity.setTokenType("BEARER");
        tokenEntity.setExpirationDate(LocalDateTime.now().plusDays(7)); // 7 ngày
        tokenEntity.setRevoked(false);
        tokenEntity.setUser(existingUser);
        tokenRepository.save(tokenEntity);
        return new SigninAdminDto(accessToken, refreshToken);
    }
}
