package com.ninos.users.controller;

import com.ninos.res.Response;
import com.ninos.users.dto.UpdatePasswordRequest;
import com.ninos.users.dto.UserDTO;
import com.ninos.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/me")
    public ResponseEntity<Response<UserDTO>> getMyUserDetails(){
        return ResponseEntity.ok(userService.getMyUserDetails());
    }

    @GetMapping("/by-id/{userId}")
    public ResponseEntity<Response<UserDTO>> getUserById(@PathVariable("userId") Long userId){
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<List<UserDTO>>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/update-password")
    public ResponseEntity<Response<?>> updatePassword(@RequestBody @Valid UpdatePasswordRequest updatePasswordRequest){
        return ResponseEntity.ok(userService.updatePassword(updatePasswordRequest));
    }


    @PutMapping("/profile-picture")
    public ResponseEntity<Response<?>> uploadProfilePicture(@RequestParam("file")MultipartFile file){
        return ResponseEntity.ok(userService.uploadProfilePicture(file));
    }



}
