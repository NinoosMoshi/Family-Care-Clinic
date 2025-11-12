package com.ninos.consultation.controller;

import com.ninos.consultation.dto.ConsultationDTO;
import com.ninos.consultation.service.ConsultationService;
import com.ninos.res.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {


    private final ConsultationService consultationService;


    @PostMapping
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<Response<ConsultationDTO>> createConsultation(@RequestBody ConsultationDTO consultationDTO){
        return ResponseEntity.ok(consultationService.createConsultation(consultationDTO));
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<Response<ConsultationDTO>> getConsultationByAppointmentId(@PathVariable Long appointmentId){
        return ResponseEntity.ok(consultationService.getConsultationByAppointmentId(appointmentId));
    }


    @GetMapping("/history")
    public ResponseEntity<Response<List<ConsultationDTO>>> getConsultationHistoryForPatient(@RequestParam(required = false) Long patientId){
        return ResponseEntity.ok(consultationService.getConsultationHistoryForPatient(patientId));
    }


}
