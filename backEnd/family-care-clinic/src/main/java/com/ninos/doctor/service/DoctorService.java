package com.ninos.doctor.service;

import com.ninos.doctor.dto.DoctorDTO;
import com.ninos.enums.BloodGroup;
import com.ninos.enums.Genotype;
import com.ninos.enums.Specialization;
import com.ninos.patient.dto.PatientDTO;
import com.ninos.res.Response;

import java.util.List;

public interface DoctorService {

    Response<DoctorDTO> getDoctorProfile();
    Response<?> updateDoctorProfile(DoctorDTO doctorDTO);
    Response<List<DoctorDTO>> getAllDoctors();
    Response<DoctorDTO> getDoctorById(Long doctorId);
    Response<List<DoctorDTO>> searchDoctorsBySpecialization(Specialization specialization);
    Response<List<Specialization>> getAllSpecializationEnums();


}
