package com.ninos.appointment.controller;

import com.ninos.appointment.dto.AppointmentDTO;
import com.ninos.appointment.service.AppointmentService;
import com.ninos.res.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;


    @PostMapping
    public ResponseEntity<Response<AppointmentDTO>> bookAppointment(@RequestBody @Valid AppointmentDTO appointmentDTO){
        System.out.println("BACKEND RECEIVED startTime = " + appointmentDTO.getStartTime());
        return ResponseEntity.ok(appointmentService.bookAppointment(appointmentDTO));
    }

    @GetMapping
    public ResponseEntity<Response<List<AppointmentDTO>>> getMyAppointments(){
        return ResponseEntity.ok(appointmentService.getMyAppointments());
    }

    @PutMapping("/cancel/{appointmentId}")
    public ResponseEntity<Response<?>> cancelAppointment(@PathVariable Long appointmentId){
        return ResponseEntity.ok(appointmentService.cancelAppointment(appointmentId));
    }

    @PutMapping("/complete/{appointmentId}")
    public ResponseEntity<Response<?>> completeAppointment(@PathVariable Long appointmentId){
        return ResponseEntity.ok(appointmentService.completeAppointment(appointmentId));
    }






}
