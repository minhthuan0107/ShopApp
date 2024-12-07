package com.project.shopapp.services;

import com.project.shopapp.dtos.UserDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.User;
import org.springframework.stereotype.Service;


public interface IUserService {
    User createUser (UserDto userDto) throws DataNotFoundException;
    User signin (String phoneNumber, String password) throws Exception;
}
