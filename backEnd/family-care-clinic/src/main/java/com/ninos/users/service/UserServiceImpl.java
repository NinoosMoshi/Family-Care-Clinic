package com.ninos.users.service;

import com.ninos.exceptions.BadRequestException;
import com.ninos.exceptions.NotFoundException;
import com.ninos.notification.dto.NotificationDTO;
import com.ninos.notification.service.NotificationService;
import com.ninos.res.Response;
import com.ninos.users.dto.UpdatePasswordRequest;
import com.ninos.users.dto.UserDTO;
import com.ninos.users.entity.User;
import com.ninos.users.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService{

    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

//    private final String uploadDir = "uploads/profile-pictures/"; // save image for backend
    private final String uploadDir = "C:/Users/ninoo/OneDrive/Desktop/doctor-app(springboot-angular)/frontEnd/family-care-clinic-ui/public/profile-picture/"; // save image for frontend


    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null){
            throw new NotFoundException("User is not authenticated");
        }
        String email = authentication.getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }


    @Override
    public Response<UserDTO> getMyUserDetails() {
        User user = getCurrentUser();
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return Response.<UserDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User details retrieved successfully")
                .data(userDTO)
                .build();
    }

    @Override
    public Response<UserDTO> getUserById(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return Response.<UserDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User retrieved successfully")
                .data(userDTO)
                .build();
    }

    @Override
    public Response<List<UserDTO>> getAllUsers() {
        List<User> users = userRepo.findAll();
        List<UserDTO> userDTOS = users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class)).toList();

        return Response.<List<UserDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All Users retrieved successfully")
                .data(userDTOS)
                .build();
    }

    @Override
    public Response<?> updatePassword(UpdatePasswordRequest updatePasswordRequest) {

        User user = getCurrentUser();
        String oldPassword = updatePasswordRequest.getOldPassword();
        String newPassword = updatePasswordRequest.getNewPassword();

        if(oldPassword == null || newPassword == null){
            throw new BadRequestException("OldPassword or NewPassword is required");
        }
        // validate the oldPassword
        if(!passwordEncoder.matches(oldPassword, user.getPassword())){
            throw new BadRequestException("Old password not correct");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        // Send password change confirmation email
        NotificationDTO updatePassword = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Your Password was successfully changed")
                .templateName("password-change")
                .templateVariables(Map.of(
                        "name", user.getName()
                ))
                .build();

        notificationService.sendEmail(updatePassword, user);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Password changed successfully")
                .build();
    }


    @Override
    public Response<?> uploadProfilePicture(MultipartFile file) {
        User user = getCurrentUser();

        try {
            Path uploadPath = Paths.get(uploadDir);  // will get the directory

            if(!Files.exists((uploadPath))){     // if this directory not exists, then create one
                Files.createDirectories(uploadPath);
            }

            if(user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()){
                Path oldFile = Paths.get(user.getProfilePictureUrl()); // it will get the image in your profile
                if(Files.exists(oldFile)){
                    Files.delete(oldFile);
                }
            }

            // Generate a unique file name to avoid conflicts

            // Extract the original name of the uploaded file (e.g., "photo.jpg")
            String originalFileName = file.getOriginalFilename();
            String fileExtension="";

            // Extract the file extension (e.g., ".jpg" or ".png") if it exists
            if(originalFileName != null && originalFileName.contains(".")){
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            // Generate a random unique file name to prevent conflicts (e.g., "c4a2b9d8-9d.png")
            String newFileName = UUID.randomUUID() + fileExtension;

            // Create the full file path where the image will be stored
            Path filePath = uploadPath.resolve(newFileName); // filePath = uploads/profile-pictures/a123b456-789c-4de0.png

            // Copy the uploaded file's data from the input stream to the target file path
            Files.copy(file.getInputStream(), filePath); // uploads/profile-pictures/a123b456-789c-4de0.png


//            String fileUrl = uploadDir + newFileName; // fileUrl = "uploads/profile-pictures/a123b456-789c-4de0.png"
              String fileUrl = "/profile-picture/" + newFileName;


            user.setProfilePictureUrl(fileUrl);
            userRepo.save(user);

            return Response.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Profile picture uploaded successfully.")
                    .data(fileUrl)
                    .build();

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

    }




}
