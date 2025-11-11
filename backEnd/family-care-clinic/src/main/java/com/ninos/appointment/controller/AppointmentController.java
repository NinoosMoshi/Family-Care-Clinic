package com.ninos.appointment.controller;

import com.ninos.appointment.dto.AppointmentDTO;
import com.ninos.appointment.service.AppointmentService;
import com.ninos.res.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;


    @PostMapping
    public ResponseEntity<Response<AppointmentDTO>> bookAppointment(@RequestBody @Valid AppointmentDTO appointmentDTO){
        return ResponseEntity.ok(appointmentService.bookAppointment(appointmentDTO));
    }


}
