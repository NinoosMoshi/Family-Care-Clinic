package com.ninos.users.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ninos.enums.Specialization;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationRequest {

    @NotBlank(message = "name is required")
    private String name;


    @Email
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private Specialization specialization;

    private String licenseNumber;

    private List<String> roles;
}
