package com.ninos.doctor.service;

import com.ninos.doctor.dto.DoctorDTO;
import com.ninos.doctor.entity.Doctor;
import com.ninos.doctor.repo.DoctorRepo;
import com.ninos.enums.Genotype;
import com.ninos.enums.Specialization;
import com.ninos.exceptions.NotFoundException;
import com.ninos.patient.dto.PatientDTO;
import com.ninos.res.Response;
import com.ninos.users.entity.User;
import com.ninos.users.repo.UserRepo;
import com.ninos.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService{

    private final DoctorRepo doctorRepo;
    private final UserRepo userRepo;
    private final UserService userService;
    private final ModelMapper modelMapper;


    @Override
    public Response<DoctorDTO> getDoctorProfile() {

        User user = userService.getCurrentUser();
        Doctor doctor = doctorRepo.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Doctor Not Found"));
        DoctorDTO doctorDTO = modelMapper.map(doctor, DoctorDTO.class);

        return Response.<DoctorDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Doctor Profile retrieved successfully")
                .data(doctorDTO)
                .build();
    }


    @Override
    public Response<?> updateDoctorProfile(DoctorDTO doctorDTO) {

        User user = userService.getCurrentUser();

        Doctor doctor = doctorRepo.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Doctor Not Found"));

        if(StringUtils.hasText(doctorDTO.getFirstName())){
            doctor.setFirstName(doctorDTO.getFirstName());
        }
        if(StringUtils.hasText(doctorDTO.getLastName())){
            doctor.setLastName(doctorDTO.getLastName());
        }

        Optional.ofNullable(doctorDTO.getSpecialization()).ifPresent(doctor::setSpecialization);

        Doctor savedDoctor = doctorRepo.save(doctor);

        //update the name of the user
        user.setName(savedDoctor.getFirstName() + " " + savedDoctor.getLastName());

        userRepo.save(user);

        return Response.<PatientDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Doctor Profile updated successfully")
                .build();
    }


    @Override
    public Response<List<DoctorDTO>> getAllDoctors() {

        List<Doctor> doctorList = doctorRepo.findAll();
        List<DoctorDTO> doctorDTOList = doctorList.stream()
                .map(doctor -> modelMapper.map(doctor,DoctorDTO.class))
                .toList();

        return Response.<List<DoctorDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All Doctors retrieved successfully")
                .data(doctorDTOList)
                .build();

    }


    @Override
    public Response<DoctorDTO> getDoctorById(Long doctorId) {

        Doctor doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor Not Found With ID: " + doctorId));
        DoctorDTO doctorDTO = modelMapper.map(doctor, DoctorDTO.class);

        return Response.<DoctorDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Doctor retrieved successfully")
                .data(doctorDTO)
                .build();

    }


    @Override
    public Response<List<DoctorDTO>> searchDoctorsBySpecialization(Specialization specialization) {

        List<Doctor> doctorList = doctorRepo.findBySpecialization(specialization);
        List<DoctorDTO> doctorDTOList = doctorList.stream()
                .map(doctor -> modelMapper.map(doctor, DoctorDTO.class))
                .toList();

        String message = doctorList.isEmpty() ?
             "No doctors found for specialization: " + specialization.name()
        :
             "Doctors retrieved successfully for specialization: " + specialization.name();


        return Response.<List<DoctorDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(message)
                .data(doctorDTOList)
                .build();
    }


    @Override
    public Response<List<Specialization>> getAllSpecializationEnums() {

        List<Specialization> specializations = Arrays.asList(Specialization.values());

        return Response.<List<Specialization>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Specializations retrieved successfully")
                .data(specializations)
                .build();
    }
}
