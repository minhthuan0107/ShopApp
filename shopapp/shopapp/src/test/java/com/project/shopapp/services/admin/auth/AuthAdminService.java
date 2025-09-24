package com.project.shopapp.services.admin.auth;

import com.project.shopapp.components.JwtTokenUtils;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.admin.auth.SigninAdminDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Role;
import com.project.shopapp.models.Token;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.TokenRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.services.admin.AuthAdminService;
import com.project.shopapp.ultis.MessageKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthAdminServiceTest {
    @InjectMocks
    private AuthAdminService authAdminService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LocalizationUtils localizationUtils;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenUtils jwtTokenUtils;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenRepository tokenRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Case 1: Đăng nhập thành công
    @Test
    void signinAdmin_success() throws Exception {
        String phone = "0123456789";
        String password = "password";

        User user = new User();
        user.setPhoneNumber(phone);
        user.setPassword("encodedPassword");

        // mock role admin
        Role role = mock(Role.class);
        when(role.isAdmin()).thenReturn(true);
        user.setRole(role);

        when(userRepository.findByPhoneNumber(phone)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(jwtTokenUtils.generateAccessToken(any())).thenReturn("access-token");
        when(jwtTokenUtils.generateRefreshToken(any())).thenReturn("refresh-token");

        SigninAdminDto result = authAdminService.signinAdmin(phone, password);

        assertNotNull(result);
        assertEquals("access-token", result.getAccessToken());
        assertEquals("refresh-token", result.getRefreshToken());
        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    // Case 2: Sai số điện thoại
    @Test
    void signinAdmin_wrongPhoneNumber() {
        String phone = "0000";
        when(userRepository.findByPhoneNumber(phone)).thenReturn(Optional.empty());
        when(localizationUtils.getLocalizedMessage(MessageKeys.WRONG_PHONE_NUMBER))
                .thenReturn("Sai số điện thoại");

        Exception ex = assertThrows(DataNotFoundException.class,
                () -> authAdminService.signinAdmin(phone, "password"));

        assertEquals("Sai số điện thoại", ex.getMessage());
    }

    // Case 3: Không phải admin
    @Test
    void signinAdmin_notAdmin() {
        String phone = "0123";
        User user = new User();
        Role role = mock(Role.class);
        when(role.isAdmin()).thenReturn(false);
        user.setRole(role);

        when(userRepository.findByPhoneNumber(phone)).thenReturn(Optional.of(user));
        when(localizationUtils.getLocalizedMessage(MessageKeys.ACCESS_DENIED))
                .thenReturn("Quyền truy cập bị từ chối");

        Exception ex = assertThrows(DataNotFoundException.class,
                () -> authAdminService.signinAdmin(phone, "password"));

        assertEquals("Quyền truy cập bị từ chối", ex.getMessage());
    }

    // Case 4: Sai mật khẩu
    @Test
    void signinAdmin_wrongPassword() {
        String phone = "0123";
        User user = new User();
        user.setPassword("encodedPassword");

        Role role = mock(Role.class);
        when(role.isAdmin()).thenReturn(true);
        user.setRole(role);

        when(userRepository.findByPhoneNumber(phone)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encodedPassword")).thenReturn(false);
        when(localizationUtils.getLocalizedMessage(MessageKeys.WRONG_PASSWORD))
                .thenReturn("Sai mật khẩu");
        Exception ex = assertThrows(BadCredentialsException.class,
                () -> authAdminService.signinAdmin(phone, "wrong"));
        assertEquals("Sai mật khẩu", ex.getMessage());
    }
}
