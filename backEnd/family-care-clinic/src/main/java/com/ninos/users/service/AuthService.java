package com.ninos.users.service;

import com.ninos.res.Response;
import com.ninos.users.dto.LoginRequest;
import com.ninos.users.dto.LoginResponse;
import com.ninos.users.dto.RegistrationRequest;
import com.ninos.users.dto.ResetPasswordRequest;

public interface AuthService {

    Response<String> register(RegistrationRequest request);
    Response<LoginResponse> login(LoginRequest loginRequest);
    Response<?> forgetPassword(String email);
    Response<?> updatePasswordViaResetCode(ResetPasswordRequest resetPassword);



}
