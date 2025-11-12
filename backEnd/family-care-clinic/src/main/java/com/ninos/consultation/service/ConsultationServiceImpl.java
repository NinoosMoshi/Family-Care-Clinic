package com.ninos.consultation.service;

import com.ninos.appointment.entity.Appointment;
import com.ninos.appointment.repo.AppointmentRepo;
import com.ninos.consultation.dto.ConsultationDTO;
import com.ninos.consultation.entity.Consultation;
import com.ninos.consultation.repo.ConsultationRepo;
import com.ninos.enums.AppointmentStatus;
import com.ninos.exceptions.BadRequestException;
import com.ninos.exceptions.NotFoundException;
import com.ninos.patient.entity.Patient;
import com.ninos.patient.repo.PatientRepo;
import com.ninos.res.Response;
import com.ninos.users.entity.User;
import com.ninos.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultationServiceImpl implements ConsultationService{

    private final ConsultationRepo consultationRepo;
    private final AppointmentRepo appointmentRepo;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final PatientRepo patientRepo;


    @Override
    public Response<ConsultationDTO> createConsultation(ConsultationDTO consultationDTO) {

        User user = userService.getCurrentUser();
        Long appointmentId = consultationDTO.getAppointmentId();

        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("appointment not found"));

        // Security check 1: must be a doctor linked to the appointment
        if(!appointment.getDoctor().getUser().getId().equals(user.getId())){
            throw new BadRequestException("You are not authorized to create notes for this consultation");
        }

        // Complete the appointment
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepo.save(appointment);

        // check: ensure a consultation doesn't already exist for this appointment
        if(consultationRepo.findByAppointmentId(appointmentId).isPresent()){
            throw new BadRequestException("Consultation notes already exist for this appointment");
        }

        Consultation consultation = Consultation.builder()
                .consultationDate(LocalDateTime.now())
                .subjectiveNotes(consultationDTO.getSubjectiveNotes())
                .objectiveFindings(consultationDTO.getObjectiveFindings())
                .assessment(consultationDTO.getAssessment())
                .plan(consultationDTO.getPlan())
                .appointment(appointment)
                .build();

        consultationRepo.save(consultation);

        return Response.<ConsultationDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Consultation notes saved successfully")
                .build();
    }

    @Override
    public Response<ConsultationDTO> getConsultationByAppointmentId(Long appointmentId) {

        Consultation consultation = consultationRepo.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new NotFoundException("Consultation notes not found for appointment ID: " + appointmentId));

        ConsultationDTO consultationDTO = modelMapper.map(consultation, ConsultationDTO.class);

        return Response.<ConsultationDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Consultation notes retrieved successfully")
                .data(consultationDTO)
                .build();
    }


    @Override
    public Response<List<ConsultationDTO>> getConsultationHistoryForPatient(Long patientId) {

        User user = userService.getCurrentUser();

        // if patientId is null, retrieve the ID of the current authenticated patient
        if(patientId == null){
            Patient currentPatient = patientRepo.findByUser(user)
                    .orElseThrow(() -> new NotFoundException("Patient not found for current user"));
            patientId = currentPatient.getId();
        }

        // Find the patient to ensure they exist
        patientRepo.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient not found"));

        // use the repository methods to fetch all consultations linked via appointments
        List<Consultation> history = consultationRepo.findByAppointmentPatientIdOrderByConsultationDateDesc(patientId);

        if(history.isEmpty()){
            return Response.<List<ConsultationDTO>>builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("No consultation history found for this patient")
                    .data(List.of())
                    .build();
        }

        List<ConsultationDTO> historyDTOs = history.stream()
                .map(consultation -> modelMapper.map(consultation, ConsultationDTO.class))
                .toList();

        return Response.<List<ConsultationDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Consultation history retrieved successfully")
                .data(historyDTOs)
                .build();
    }
}
