package com.ninos.patient.service;

import com.ninos.enums.BloodGroup;
import com.ninos.enums.Genotype;
import com.ninos.exceptions.NotFoundException;
import com.ninos.patient.dto.PatientDTO;
import com.ninos.patient.entity.Patient;
import com.ninos.patient.repo.PatientRepo;
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
public class PatientServiceImpl implements PatientService{

    private final PatientRepo patientRepo;
    private final UserRepo userRepo;
    private final UserService userService;
    private final ModelMapper modelMapper;


    @Override
    public Response<PatientDTO> getPatientProfile() {

        User user = userService.getCurrentUser();

        Patient patient = patientRepo.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Patient Not Found"));
        PatientDTO patientDTO = modelMapper.map(patient, PatientDTO.class);

        return Response.<PatientDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Patient Profile retrieved successfully")
                .data(patientDTO)
                .build();
    }

    @Override
    public Response<?> updatePatientProfile(PatientDTO patientDTO) {

        User user = userService.getCurrentUser();

        Patient patient = patientRepo.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Patient Not Found"));

        if(StringUtils.hasText(patientDTO.getFirstName())){
            patient.setFirstName(patientDTO.getFirstName());
        }
        if(StringUtils.hasText(patientDTO.getLastName())){
            patient.setLastName(patientDTO.getLastName());
        }
        if(StringUtils.hasText(patientDTO.getPhone())){
            patient.setPhone(patientDTO.getPhone());
        }

        Optional.ofNullable(patientDTO.getDateOfBirth()).ifPresent(patient::setDateOfBirth);

        if(StringUtils.hasText(patientDTO.getKnownAllergies())){
            patient.setKnownAllergies(patientDTO.getKnownAllergies());
        }

        Optional.ofNullable(patientDTO.getBloodGroup()).ifPresent(patient::setBloodGroup);
        Optional.ofNullable(patientDTO.getGenotype()).ifPresent(patient::setGenotype);

        Patient savedPatient =  patientRepo.save(patient);

        //update the name of the user
        user.setName(savedPatient.getFirstName() + " " + savedPatient.getLastName());

        userRepo.save(user);

        return Response.<PatientDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Patient Profile updated successfully")
                .build();
    }

    @Override
    public Response<PatientDTO> getPatientById(Long patientId) {

        Patient patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient Not Found With ID: " + patientId));
        PatientDTO patientDTO = modelMapper.map(patient, PatientDTO.class);

        return Response.<PatientDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Patient retrieved successfully")
                .data(patientDTO)
                .build();
    }


    @Override
    public Response<List<BloodGroup>> getAllBloodGroupEnums() {
        List<BloodGroup> bloodGroupList = Arrays.asList(BloodGroup.values());

        return Response.<List<BloodGroup>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("BloodGroups retrieved successfully")
                .data(bloodGroupList)
                .build();
    }

    @Override
    public Response<List<Genotype>> getAllGenotypeEnums() {
        List<Genotype> genotypeList = Arrays.asList(Genotype.values());

        return Response.<List<Genotype>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("GenoTypes retrieved successfully")
                .data(genotypeList)
                .build();
    }
}
