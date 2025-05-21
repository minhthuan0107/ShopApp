package com.project.shopapp.services.user;

import com.project.shopapp.dtos.user.ChangePasswordDto;
import com.project.shopapp.dtos.user.SigninDto;
import com.project.shopapp.dtos.user.UpdateProfileDto;
import com.project.shopapp.dtos.user.UserDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.User;
import com.project.shopapp.responses.user.UserResponse;


public interface IUserService {
    User createUser (UserDto userDto) throws DataNotFoundException;
    SigninDto signin (String phoneNumber, String password) throws Exception;
    User  getUserByPhoneNumber (String phoneNumber) throws Exception;
   void changePassword (String phoneNumber,ChangePasswordDto changePasswordDto) throws Exception;

   UserResponse updateProfile  (String phoneNumber, UpdateProfileDto updateProfileDto) throws Exception;
}
