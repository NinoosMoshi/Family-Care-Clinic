package com.ninos.users.service;

import com.ninos.res.Response;
import com.ninos.users.dto.UpdatePasswordRequest;
import com.ninos.users.dto.UserDTO;
import com.ninos.users.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

     User getCurrentUser();
     Response<UserDTO> getMyUserDetails();
     Response<UserDTO> getUserById(Long userId);
     Response<List<UserDTO>> getAllUsers();
     Response<?> updatePassword(UpdatePasswordRequest updatePasswordRequest);
     Response<?> uploadProfilePicture(MultipartFile file);

}
