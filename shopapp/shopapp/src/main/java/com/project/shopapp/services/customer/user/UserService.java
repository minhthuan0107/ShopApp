package com.project.shopapp.services.customer.user;

import com.project.shopapp.components.JwtTokenUtils;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.configurations.UserDetailsServiceImpl;
import com.project.shopapp.dtos.customer.social.SocialLoginDto;
import com.project.shopapp.dtos.customer.user.ChangePasswordDto;
import com.project.shopapp.dtos.customer.user.SigninDto;
import com.project.shopapp.dtos.customer.user.UpdateProfileDto;
import com.project.shopapp.dtos.customer.user.UserDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Role;
import com.project.shopapp.models.Token;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.RoleRepository;
import com.project.shopapp.repositories.TokenRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.responses.user.UserResponse;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Override
    public User getUserById(Long userId) throws Exception {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND)));
    }

    @Override
    @Transactional
    public User createUser(UserDto userDto) throws DataNotFoundException {
        //register User
        String phoneNumber = userDto.getPhoneNumber();
        //Kiem tra sdt da ton tai hay chua
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataIntegrityViolationException(
                    localizationUtils.getLocalizedMessage(MessageKeys.PHONENUMBER_ALREADY_EXISTS));
        }
        if (!userDto.getPassword().equals(userDto.getRetypePassword())) {
            throw new IllegalArgumentException(
                    localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH));
        }
        //convert userDto => user
        User newUser = User.builder().
                fullname(userDto.getFullName())
                .phoneNumber(userDto.getPhoneNumber())
                .password(userDto.getPassword())
                .address(userDto.getAddress())
                .dateOfBirth(userDto.getDateOfBirth())
                .facebookAccountId(userDto.getFacebookAccountId())
                .googleAccountId(userDto.getGoogleAccountId())
                .isActive(true)
                .build();
        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new DataNotFoundException("Role not found"));
        newUser.setRole(role);
        if (userDto.getFacebookAccountId() == null && userDto.getGoogleAccountId() == null) {
            String password = userDto.getPassword();
            String encodePassword = passwordEncoder.encode(password);
            newUser.setPassword(encodePassword);
        }
        return userRepository.save(newUser);
    }

    @Override
    @Transactional
    public SigninDto signin(String phoneNumber, String password) throws Exception {
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.WRONG_PHONE_NUMBER));
        }
        User existingUser = optionalUser.get();
        if (isLinkedWithSocialAccount(existingUser) && !passwordEncoder.matches(password, existingUser.getPassword())) {
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

        return new SigninDto(accessToken, refreshToken);
    }

    //Hàm check user có phải là tài khoản google hay facebook
    private boolean isLinkedWithSocialAccount(User user) {
        return (user.getFacebookAccountId() == null || user.getFacebookAccountId().isBlank())
                && (user.getGoogleAccountId() == null || user.getGoogleAccountId().isBlank());
    }

    @Override
    @Transactional
    public SigninDto signinSocial(SocialLoginDto socialLoginDto) throws Exception {
        User user = userRepository.findByGoogleAccountId(socialLoginDto.getGoogleAccountId())
                .map(existingUser -> {
                    existingUser.setFullname(socialLoginDto.getFullname());
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setGoogleAccountId(socialLoginDto.getGoogleAccountId());
                    newUser.setFullname(socialLoginDto.getFullname());
                    Role role = roleRepository.findByName("USER").get();
                    newUser.setRole(role);
                    return userRepository.save(newUser);
                });
        UserDetails userDetails = userDetailsServiceImpl.loadUserByGoogleAccountId(user.getGoogleAccountId());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        String accessToken = jwtTokenUtils.generateAccessToken(authentication);
        String refreshToken = jwtTokenUtils.generateRefreshToken(authentication);

        Token tokenEntity = new Token();
        tokenEntity.setToken(refreshToken); // Lưu refreshToken
        tokenEntity.setTokenType("BEARER");
        tokenEntity.setExpirationDate(LocalDateTime.now().plusDays(7)); // 7 ngày
        tokenEntity.setRevoked(false);
        tokenEntity.setUser(user);
        tokenRepository.save(tokenEntity);
        return new SigninDto(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public void changePassword(Long userId,ChangePasswordDto changePasswordDto) throws Exception {
        User user = getUserById(userId);
        if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword())) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(
                    MessageKeys.WRONG_PASSWORD));
        }
        if (changePasswordDto.getCurrentPassword().equals(changePasswordDto.getNewPassword())) {
            throw new IllegalArgumentException(localizationUtils.getLocalizedMessage(
                    MessageKeys.NEW_PASSWORD_SAME_AS_CURRENT));
        }
        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(user);
    }
    @Transactional
    @Override
    public UserResponse updateProfile(Long userId ,UpdateProfileDto updateProfileDto) throws Exception {
        User user = getUserById(userId);
        if (updateProfileDto.getFullName() != null) {
            user.setFullname(updateProfileDto.getFullName());
        }
        if (updateProfileDto.getAddress() != null) {
            user.setAddress(updateProfileDto.getAddress());
        }
        if (updateProfileDto.getDateOfBirth() != null) {
            user.setDateOfBirth(updateProfileDto.getDateOfBirth());
        }
        User updateUser = userRepository.save(user);
        return UserResponse.fromUser(updateUser);
    }
}
