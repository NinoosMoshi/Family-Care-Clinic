package com.ninos.patient.controller;

import com.ninos.enums.BloodGroup;
import com.ninos.enums.Genotype;
import com.ninos.patient.dto.PatientDTO;
import com.ninos.patient.service.PatientService;
import com.ninos.res.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/patients")
public class PatientController {

     private final PatientService patientService;



     @GetMapping("/me")
     @PreAuthorize("hasAuthority('PATIENT')")
     ResponseEntity<Response<PatientDTO>> getPatientProfile(){
          return ResponseEntity.ok(patientService.getPatientProfile());
     }


     @PutMapping("/me")
     @PreAuthorize("hasAuthority('PATIENT')")
     ResponseEntity<Response<?>> updatePatientProfile(@RequestBody PatientDTO patientDTO){
         return ResponseEntity.ok(patientService.updatePatientProfile(patientDTO));
     }


     @GetMapping("/{patientId}")
     ResponseEntity<Response<PatientDTO>> getPatientById(@PathVariable Long patientId){
          return ResponseEntity.ok(patientService.getPatientById(patientId));
     }


     @GetMapping("/blood-group")
     ResponseEntity<Response<List<BloodGroup>>> getAllBloodGroupEnums(){
          return ResponseEntity.ok(patientService.getAllBloodGroupEnums());
     }

     @GetMapping("/genotype")
     ResponseEntity<Response<List<Genotype>>> getAllGenotypeEnums(){
          return ResponseEntity.ok(patientService.getAllGenotypeEnums());
     }


}
