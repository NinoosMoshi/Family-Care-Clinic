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

        User user = userService.getCurrentUser();
        Long userId = user.getId();
        List<Appointment> appointments;

        // Check Doctor's role
        boolean isDoctor = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("DOCTOR"));

        if(isDoctor){
            // 1. Check doctor profile existence
            doctorRepo.findByUser(user).orElseThrow(() -> new NotFoundException("Doctor profile not found"));

            // 2. Efficiently fetch appointments of the doctor
            appointments = appointmentRepo.findByDoctor_User_IdOrderByIdDesc(userId);
        }else{
            // 1. Check patient profile existence
            patientRepo.findByUser(user).orElseThrow(() -> new NotFoundException("Patient profile not found"));

            // 2. Efficiently fetch appointments using user id to navigate patient
            appointments = appointmentRepo.findByPatient_User_IdOrderByIdDesc(userId);
        }

        List<AppointmentDTO> appointmentDTOList = appointments.stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentDTO.class))
                .toList();

        return Response.<List<AppointmentDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Appointments retrieved successfully")
                .data(appointmentDTOList)
                .build();
    }


    @Override
    public Response<?> cancelAppointment(Long appointmentId) {

        User user = userService.getCurrentUser();

        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));

        // Add security check: only the patient or doctor involved can cancel
        boolean isOwner = appointment.getPatient().getUser().getId().equals(user.getId()) ||
                          appointment.getDoctor().getUser().getId().equals(user.getId());

        if(!isOwner){
            throw new BadRequestException("You don't have a permission to cancel this appointment");
        }

        // update status
        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment savedAppointment = appointmentRepo.save(appointment);

        // NOTE: Notification should be sent to the other party (patient/doctor)
        sendAppointmentCancellation(savedAppointment, user);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Appointments cancelled successfully")
                .build();
    }


    @Override
    public Response<?> completeAppointment(Long appointmentId) {

        // get current user(must be a doctor)
        User currentUser = userService.getCurrentUser();

        // 1. fetch the appointment
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not fount with ID: " + appointmentId));

        // Security Check: 1: Ensure the current user is a doctor assigned to this appointment
        if(!appointment.getDoctor().getUser().getId().equals(currentUser.getId())){
            throw new BadRequestException("Only Doctor can assigned to this appointment to complete");
        }

        // 2. update status and end time
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setEndTime(LocalDateTime.now());

        appointmentRepo.save(appointment);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Appointment complete successfully")
                .build();
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


    private void sendAppointmentCancellation(Appointment appointment, User cancelingUser){

        User patientUser = appointment.getPatient().getUser();
        User doctorUser = appointment.getDoctor().getUser();

        // Safety check to ensure the cancellingUser is involved
        boolean isOwner = patientUser.getId().equals(cancelingUser.getId()) ||
                          doctorUser.getId().equals(cancelingUser.getId());

        if(!isOwner){
            log.error("Cancellation initiated by user not associated with appointment. User ID: {}", cancelingUser.getId());
            return;
        }

        String formattedTime = appointment.getStartTime().format(FORMATTER);
        String cancellingPartyName = cancelingUser.getName();

        Map<String, Object> baseVars = new HashMap<>();
        baseVars.put("cancellingPartyName", cancellingPartyName);
        baseVars.put("appointmentTime", formattedTime);
        baseVars.put("doctorName", appointment.getDoctor().getLastName());
        baseVars.put("patientFullName", patientUser.getName());

        Map<String, Object> doctorVars = new HashMap<>(baseVars);
        baseVars.put("recipientName", doctorUser.getName());

        NotificationDTO doctorNotification = NotificationDTO.builder()
                .recipient(doctorUser.getEmail())
                .subject("Family Care Clinic: Appointment Cancellation")
                .templateName("appointment-cancellation")
                .templateVariables(doctorVars)
                .build();

        notificationService.sendEmail(doctorNotification, doctorUser);
        log.info("Dispatched cancellation email to doctor: {}", doctorUser.getEmail());


        Map<String, Object> patientVars = new HashMap<>(baseVars);
        baseVars.put("recipientName", patientUser.getName());

        NotificationDTO patientNotification = NotificationDTO.builder()
                .recipient(patientUser.getEmail())
                .subject("Family Care Clinic: Appointment Cancellation (ID: " + appointment.getId() + ")")
                .templateName("appointment-cancellation")
                .templateVariables(patientVars)
                .build();

        notificationService.sendEmail(patientNotification, patientUser);
        log.info("Dispatched cancellation email to patient: {}", patientUser.getEmail());

    }



}
