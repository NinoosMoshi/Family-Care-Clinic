package com.ninos.users.controller;

import com.ninos.res.Response;
import com.ninos.users.dto.LoginRequest;
import com.ninos.users.dto.LoginResponse;
import com.ninos.users.dto.RegistrationRequest;
import com.ninos.users.dto.ResetPasswordRequest;
import com.ninos.users.entity.PasswordResetCode;
import com.ninos.users.repo.PasswordResetRepo;
import com.ninos.users.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    ResponseEntity<Response<String>> register(@RequestBody @Valid RegistrationRequest request){
       return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    ResponseEntity<Response<LoginResponse>> login(@RequestBody @Valid LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }


    @PostMapping("/forgot-password")
    ResponseEntity<Response<?>> forgetPassword(@RequestBody ResetPasswordRequest request){
        return ResponseEntity.ok(authService.forgetPassword(request.getEmail()));
    }


    @PostMapping("/reset-password")
    ResponseEntity<Response<?>> resetPassword(@RequestBody ResetPasswordRequest request){
        return ResponseEntity.ok(authService.updatePasswordViaResetCode(request));
    }




}
