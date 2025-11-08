package com.ninos.patient.service;

import com.ninos.enums.BloodGroup;
import com.ninos.enums.Genotype;
import com.ninos.patient.dto.PatientDTO;
import com.ninos.res.Response;

import java.util.List;

public interface PatientService {

    Response<PatientDTO> getPatientProfile();
    Response<?> updatePatientProfile(PatientDTO patientDTO);
    Response<PatientDTO> getPatientById(Long patientId);
    Response<List<BloodGroup>> getAllBloodGroupEnums();
    Response<List<Genotype>> getAllGenotypeEnums();


}
