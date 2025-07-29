package com.project.shopapp.services.customer.user;

import com.project.shopapp.dtos.customer.social.SocialLoginDto;
import com.project.shopapp.dtos.customer.user.ChangePasswordDto;
import com.project.shopapp.dtos.customer.user.SigninDto;
import com.project.shopapp.dtos.customer.user.UpdateProfileDto;
import com.project.shopapp.dtos.customer.user.UserDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.User;
import com.project.shopapp.responses.customer.user.UserResponse;


public interface IUserService {
    User createUser (UserDto userDto) throws DataNotFoundException;
    SigninDto signin (String phoneNumber, String password) throws Exception;

    SigninDto signinSocial (SocialLoginDto socialLoginDto) throws Exception ;
    User  getUserById (Long userId)  throws Exception;
   void changePassword (Long userId,ChangePasswordDto changePasswordDto) throws Exception;

   UserResponse updateProfile  (Long userId, UpdateProfileDto updateProfileDto) throws Exception;
}
