package com.ninos.appointment.service;

import com.ninos.appointment.dto.AppointmentDTO;
import com.ninos.appointment.entity.Appointment;
import com.ninos.appointment.repo.AppointmentRepo;
import com.ninos.doctor.entity.Doctor;
import com.ninos.doctor.repo.DoctorRepo;
import com.ninos.enums.AppointmentStatus;
import com.ninos.exceptions.BadRequestException;
import com.ninos.exceptions.NotFoundException;
import com.ninos.notification.dto.NotificationDTO;
import com.ninos.notification.service.NotificationService;
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
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService{

    private final AppointmentRepo appointmentRepo;
    private final DoctorRepo doctorRepo;
    private final PatientRepo patientRepo;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final NotificationService notificationService;
                                                                              //Saturday, Nov 08, 2025 at  06:25 PM
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy 'at' hh:mm a");


    @Override
    public Response<AppointmentDTO> bookAppointment(AppointmentDTO appointmentDTO) {

        User currentUser = userService.getCurrentUser();

        // 1. Find the patient who is booking the appointment
        Patient patient = patientRepo.findByUser(currentUser)
                .orElseThrow(() -> new NotFoundException("Patient Not Found"));

        // 2. Find the doctor the patient wants to book
        Doctor doctor = doctorRepo.findById(appointmentDTO.getDoctorId())
                .orElseThrow(() -> new NotFoundException("Doctor Not Found"));

        // --- START: VALIDATION LOGIC ---
        // Set up the appointment start and end times
        LocalDateTime startTime = appointmentDTO.getStartTime();
        LocalDateTime endTime = startTime.plusMinutes(60); // Assuming 60-min slot

        // 3. Make sure the appointment is at least 1 hour from now
        if(startTime.isBefore(LocalDateTime.now().plusHours(1))){
            throw new BadRequestException("Appointment must be booked at least 1 hour in advance");
        }

        // Get the time that is 1 hour before the appointment
        // This ensures the doctor has a 1-hour break before this appointment
        LocalDateTime checkStart = startTime.minusMinutes(60); // minusMinutes(60): if time is 8:30 it will do 7:30. mean one hour earlier


        // Find any existing appointments that might overlap with this time
        // This checks for any scheduled appointments for this doctor
        // that happen between 1 hour before the new start time and the new end time
        List<Appointment> conflicts = appointmentRepo.findConflictingAppointments(
                doctor.getId(),
                checkStart, // Start checking 1 hour before the proposed start time
                endTime     // Until the new appointment's end time
        );

        if(!conflicts.isEmpty()){
            throw new BadRequestException("Doctor is not available at the requested time. Please check their schedule.");
        }


        // 4a. Generate a unique, random string for the room name.
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String uniqueRoomName = "care-" + uuid.substring(0,10);

        // 4b. Use the public Jitsi meet domain with your unique room name
        String meetingLink = "https://meet.jit.si/" + uniqueRoomName;
        log.info("Generate Jitsi meeting link: {}", meetingLink);

        // 5. Build and Save Appointment
        Appointment appointment = Appointment.builder()
                .startTime(appointmentDTO.getStartTime())
                .endTime(appointmentDTO.getStartTime().plusMinutes(60))
                .meetingLink(meetingLink)
                .initialSymptoms(appointmentDTO.getInitialSymptoms())
                .purposeOfConsultation(appointmentDTO.getPurposeOfConsultation())
                .status(AppointmentStatus.SCHEDULED)
                .doctor(doctor)
                .patient(patient)
                .build();

        Appointment savedAppointment = appointmentRepo.save(appointment);

        sendAppointmentConfirmation(savedAppointment);

        return Response.<AppointmentDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Appointment booked successfully")
                .build();

    }

    @Override
    public Response<List<AppointmentDTO>> getMyAppointments() {
        return null;
    }

    @Override
    public Response<AppointmentDTO> cancelAppointment(Long appointmentId) {
        return null;
    }

    @Override
    public Response<?> completeAppointment(Long appointmentId) {
        return null;
    }



    // ........METHODS.........
    private void sendAppointmentConfirmation(Appointment appointment){

        // 1. Prepare Patient Notification
        User patientUser = appointment.getPatient().getUser();
        String formattedTime = appointment.getStartTime().format(FORMATTER);

        Map<String, Object> patientVars = new HashMap<>();

        patientVars.put("patientName", patientUser.getName());
        patientVars.put("doctorName", appointment.getDoctor().getUser().getName());
        patientVars.put("appointmentTime", formattedTime);
        patientVars.put("isVirtual", true);
        patientVars.put("meetingLink", appointment.getMeetingLink());
        patientVars.put("purposeOfConsultation", appointment.getPurposeOfConsultation());

        NotificationDTO patientNotification = NotificationDTO.builder()
                .recipient(patientUser.getEmail())
                .subject("Family Care Clinic: Your Appointment is confirmed")
                .templateName("patient-appointment")
                .templateVariables(patientVars)
                .build();

        // Dispatch patient email using the low-level service
        notificationService.sendEmail(patientNotification, patientUser);
        log.info("Dispatched confirmation email for patient: {}", patientUser.getEmail());

        // 2. Prepare Doctor Notification
        User doctorUser = appointment.getDoctor().getUser();

        Map<String, Object> doctorVars = new HashMap<>();

        doctorVars.put("doctorName", doctorUser.getName());
        doctorVars.put("patientFullName", patientUser.getName());
        doctorVars.put("appointmentTime", formattedTime);
        doctorVars.put("isVirtual", true);
        doctorVars.put("meetingLink", appointment.getMeetingLink());
        doctorVars.put("initialSymptoms", appointment.getInitialSymptoms());
        doctorVars.put("purposeOfConsultation", appointment.getPurposeOfConsultation());


        NotificationDTO doctorNotification = NotificationDTO.builder()
                .recipient(doctorUser.getEmail())
                .subject("Family Care Clinic: New Appointment booked")
                .templateName("doctor-appointment")
                .templateVariables(doctorVars)
                .build();

        // Dispatch doctor email using the low-level service
        notificationService.sendEmail(doctorNotification, doctorUser);
        log.info("Dispatched new appointment email for doctor: {}", doctorUser.getEmail());

    }
}
