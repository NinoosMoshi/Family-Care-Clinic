package com.ninos.appointment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ninos.consultation.entity.Consultation;
import com.ninos.doctor.dto.DoctorDTO;
import com.ninos.enums.AppointmentStatus;
import com.ninos.patient.dto.PatientDTO;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentDTO {

    private Long id;

    @NotNull(message = "Doctor ID is required for booking an appointment")
    private Long doctorId;

    private String purposeOfConsultation;

    private String initialSymptoms;


    @NotNull(message = "Start time is required for the appointment")
    @Future(message = "Appointment must be scheduled for a future date and time") // @Future: you can not select time before date
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String meetingLink;

    private AppointmentStatus status;

    private DoctorDTO doctor;

    private PatientDTO patient;





}
