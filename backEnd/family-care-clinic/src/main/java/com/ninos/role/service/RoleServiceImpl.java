package com.ninos.role.service;

import com.ninos.exceptions.NotFoundException;
import com.ninos.res.Response;
import com.ninos.role.entity.Role;
import com.ninos.role.repo.RoleRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService{

    private final RoleRepo roleRepo;


    @Override
    public Response<Role> createRole(Role roleRequest) {

        Role savedRole = roleRepo.save(roleRequest);
        return Response.<Role>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role created successfully!")
                .data(savedRole)
                .build();
    }


    @Override
    public Response<Role> updateRole(Role roleRequest) {

        Role role = roleRepo.findById(roleRequest.getId())
                .orElseThrow(() -> new NotFoundException("Role Not Found"));

        role.setName(roleRequest.getName());

        Role updatedRole = roleRepo.save(role);
        return Response.<Role>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role updated successfully!")
                .data(updatedRole)
                .build();
    }


    @Override
    public Response<List<Role>> getAllRoles() {

        List<Role> roleList = roleRepo.findAll();
        return Response.<List<Role>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Roles retrieved successfully!")
                .data(roleList)
                .build();
    }


    @Override
    public Response<?> deleteRole(Long roleId) {

        if(!roleRepo.existsById(roleId)){
            throw new NotFoundException("Role Not Found");
        }

        roleRepo.deleteById(roleId);
        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role deleted successfully!")
                .build();
    }
}
