package com.project.shopapp.services.user;

import com.project.shopapp.dtos.UserDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.User;
import org.springframework.stereotype.Service;


public interface IUserService {
    User createUser (UserDto userDto) throws DataNotFoundException;
    String signin (String phoneNumber, String password) throws Exception;
    User  getUserByPhoneNumber (String phoneNumber) throws Exception;
}
