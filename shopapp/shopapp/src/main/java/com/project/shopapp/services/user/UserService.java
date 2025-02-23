package com.project.shopapp.services.user;

import com.project.shopapp.components.JwtTokenUtils;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.UserDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Role;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.RoleRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    @Override
    public User getUserByPhoneNumber (String phoneNumber) throws Exception{
        return userRepository.findByPhoneNumber(phoneNumber)
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
        if (userDto.getFacebookAccountId() == 0 && userDto.getGoogleAccountId() == 0) {
            String password = userDto.getPassword();
            String encodePassword = passwordEncoder.encode(password);
            newUser.setPassword(encodePassword);
        }
        return userRepository.save(newUser);
    }

    @Override
    @Transactional
    public String signin(String phoneNumber, String password) throws Exception {
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
        return jwtTokenUtils.generateJwtToken(authentication);
    }
    //Hàm check user có phải là tài khoản google hay facebook
    private boolean isLinkedWithSocialAccount(User user) {
        return user.getFacebookAccountId() == 0 && user.getGoogleAccountId() == 0;
    }
}
