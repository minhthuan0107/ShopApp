package com.project.shopapp.services.admin;

import com.project.shopapp.dtos.admin.auth.SigninAdminDto;

public interface IAuthAdminService {

    SigninAdminDto signinAdmin (String phoneNumber, String password) throws Exception;



}
