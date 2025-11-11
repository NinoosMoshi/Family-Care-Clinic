package com.ninos.appointment.service;

import com.ninos.appointment.dto.AppointmentDTO;
import com.ninos.res.Response;

import java.util.List;

public interface AppointmentService {

    Response<AppointmentDTO> bookAppointment(AppointmentDTO appointmentDTO);

    Response<List<AppointmentDTO>> getMyAppointments();

    Response<?> cancelAppointment(Long appointmentId);

    Response<?> completeAppointment(Long appointmentId);

}
