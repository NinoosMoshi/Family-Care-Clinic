package com.ninos.users.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ninos.role.entity.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // if any field is null in DB then the response will ignore that field
@JsonIgnoreProperties(ignoreUnknown = true) // ignore the data that does not have a value
public class UserDTO {

    private Long id;
    private String name;
    private String email;

    @JsonIgnore   // response will not have a password, but we can use password for request
    private String password;

    private String profilePictureUrl;

    private List<Role> roles;


}
