package com.ninos.doctor.controller;

import com.ninos.doctor.dto.DoctorDTO;
import com.ninos.doctor.service.DoctorService;
import com.ninos.enums.BloodGroup;
import com.ninos.enums.Specialization;
import com.ninos.patient.dto.PatientDTO;
import com.ninos.res.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;


    @GetMapping("/me")
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<Response<DoctorDTO>> getDoctorProfile(){
        return ResponseEntity.ok(doctorService.getDoctorProfile());
    }

    @PutMapping("/me")
    @PreAuthorize("hasAuthority('DOCTOR')")
    ResponseEntity<Response<?>> updateDoctorProfile(@RequestBody DoctorDTO doctorDTO){
        return ResponseEntity.ok(doctorService.updateDoctorProfile(doctorDTO));
    }


    @GetMapping("/all")
    public ResponseEntity<Response<List<DoctorDTO>>> getAllDoctors(){
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/{doctorId}")
    ResponseEntity<Response<DoctorDTO>> getDoctorById(@PathVariable Long doctorId){
        return ResponseEntity.ok(doctorService.getDoctorById(doctorId));
    }

    @GetMapping("/filter")
    ResponseEntity<Response<List<DoctorDTO>>> searchDoctorsBySpecialization(@RequestParam(required = true) Specialization specialization){
        return ResponseEntity.ok(doctorService.searchDoctorsBySpecialization(specialization));
    }


    @GetMapping("/specializations")
    ResponseEntity<Response<List<Specialization>>> getAllSpecializationEnums(){
        return ResponseEntity.ok(doctorService.getAllSpecializationEnums());
    }

}
