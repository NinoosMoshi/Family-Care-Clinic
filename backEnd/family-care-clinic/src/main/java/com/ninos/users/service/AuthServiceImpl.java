package com.ninos.users.service;

import com.ninos.doctor.entity.Doctor;
import com.ninos.doctor.repo.DoctorRepo;
import com.ninos.exceptions.BadRequestException;
import com.ninos.exceptions.NotFoundException;
import com.ninos.notification.dto.NotificationDTO;
import com.ninos.notification.service.NotificationService;
import com.ninos.patient.entity.Patient;
import com.ninos.patient.repo.PatientRepo;
import com.ninos.res.Response;
import com.ninos.role.entity.Role;
import com.ninos.role.repo.RoleRepo;
import com.ninos.security.JwtService;
import com.ninos.users.dto.LoginRequest;
import com.ninos.users.dto.LoginResponse;
import com.ninos.users.dto.RegistrationRequest;
import com.ninos.users.entity.PasswordResetCode;
import com.ninos.users.entity.User;
import com.ninos.users.repo.PasswordResetRepo;
import com.ninos.users.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final NotificationService notificationService;

    private final PatientRepo patientRepo;
    private final DoctorRepo doctorRepo;

    private final CodeGenerator codeGenerator;
    private final PasswordResetRepo passwordResetRepo;

    @Value("${password.reset.link}")
    private String resetLink;

    @Value("${login.link}")
    private String loginLink;



    @Override
    public Response<String> register(RegistrationRequest request) {
        // check if user already exists
        if(userRepo.findByEmail(request.getEmail()).isPresent()){
           throw new BadRequestException("User with email " + request.getEmail() + " already exists");
        }
        /// 1. determine the roles to assign. default to PATIENT if none are provided
        List<String> requestedRoleNames = (request.getRoles() != null && !request.getRoles().isEmpty())
                                          ? request.getRoles().stream().map(String::toUpperCase).toList()
                                          : List.of("PATIENT");

        boolean isDoctor = requestedRoleNames.contains("DOCTOR");

        if(isDoctor && (request.getLicenseNumber() == null || request.getLicenseNumber().isBlank())){
            throw new BadRequestException("License number required to register a doctor");
        }

        /// 2. Load and validate roles from database
        List<Role> roles = requestedRoleNames.stream()
                .map(roleRepo::findByName)
                .flatMap(Optional::stream) // skipping any roles not found in the database
                .toList();

        if(roles.isEmpty()){
            throw new NotFoundException("Registration failed: Requested roles were not found in the database");
        }

        /// 3. Create and save new user entity
        User newUser = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .roles(roles)
                .build();

        User savedUser = userRepo.save(newUser);
        log.info("New user registered: {} with {} roles.",request.getEmail(), roles.size());

        /// 4. Process Profile Creation
        for(Role role : roles){
            String roleName = role.getName();

            switch (roleName){
                case "PATIENT":
                    createPatientProfile(savedUser);
                    log.info("Patient profile created: {}", savedUser.getEmail());
                    break;

                case "DOCTOR":
                    createDoctorProfile(request,savedUser);
                    log.info("Doctor profile created: {}", savedUser.getEmail());
                    break;

                case "ADMIN":
                    log.info("Admin role assigned to user: {}", savedUser.getEmail());
                    break;

                default:
                    log.warn("Assigned role '{}' has no corresponding profile creation logic.", roleName);
                    break;
            }
        }

        /// 5. Send welcome email out
        sendRegistrationEmail(request, savedUser);


        /// 6. Return success response
        return Response.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User Register Successfully. A welcome email has been sent to you")
                .data(savedUser.getEmail())
                .build();
    }




    @Override
    public Response<LoginResponse> login(LoginRequest loginRequest) {

        User userDB = userRepo.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if(!passwordEncoder.matches(loginRequest.getPassword(), userDB.getPassword())){
           throw new BadRequestException("password does not matched");
        }

        String token = jwtService.generateToken(userDB.getEmail());
        List<String> roles = userDB.getRoles().stream().map(Role::getName).toList();

        LoginResponse loginResponse = LoginResponse.builder()
                .token(token)
                .roles(roles)
                .build();

        return Response.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User Login Successfully")
                .data(loginResponse)
                .build();
    }



    @Override
    public Response<?> forgetPassword(String email) {

        User userDB = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        passwordResetRepo.deleteByUserId(userDB.getId());

        String code = codeGenerator.generateUniqueCode();

        PasswordResetCode passwordResetCode = PasswordResetCode.builder()
                .code(code)
                .user(userDB)
                .expiryDate(calculateExpiryDate())
                .used(false)
                .build();

        passwordResetRepo.save(passwordResetCode);

        // send email reset link
        NotificationDTO passwordResetEmail = NotificationDTO.builder()
                .recipient(userDB.getEmail())
                .subject("Password Reset Code")
                .templateName("password-reset")
                .templateVariables(Map.of(
                        "name", userDB.getName(),
                        "resetLink",resetLink + code
                ))
                .build();

        notificationService.sendEmail(passwordResetEmail, userDB);

        return Response.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Password reset code send to your email")
                .build();
    }



    @Override
    public Response<?> updatePasswordViaResetCode(String email) {
        return null;
    }


    // PRIVATE METHODS
    private void createPatientProfile(User user){
        Patient patient = Patient.builder()
                .user(user)
                .build();
        patientRepo.save(patient);
        log.info("patient profile created");
    }


    private void createDoctorProfile(RegistrationRequest request, User user){
        Doctor doctor = Doctor.builder()
                .specialization(request.getSpecialization())
                .licenseNumber(request.getLicenseNumber())
                .user(user)
                .build();
        doctorRepo.save(doctor);
        log.info("doctor profile created");
    }


    private void sendRegistrationEmail(RegistrationRequest request, User user){
        NotificationDTO welcomeEmail = NotificationDTO.builder()
                .recipient(request.getEmail())
                .subject("Welcome To Family Care Clinic")
                .templateName("welcome.html")
                .message("Thank you for registering, your account is ready")
                .templateVariables(Map.of(
                        "name",request.getName(),
                            "loginLink",loginLink
                ))
                .build();

        notificationService.sendEmail(welcomeEmail, user);
    }


    public LocalDateTime calculateExpiryDate(){
         return LocalDateTime.now().plusHours(5);
    }


}
