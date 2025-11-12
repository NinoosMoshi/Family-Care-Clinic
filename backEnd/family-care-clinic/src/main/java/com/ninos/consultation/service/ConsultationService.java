package com.ninos.consultation.service;

import com.ninos.consultation.dto.ConsultationDTO;
import com.ninos.res.Response;

import java.util.List;

public interface ConsultationService {

    Response<ConsultationDTO> createConsultation(ConsultationDTO consultationDTO);

    Response<ConsultationDTO> getConsultationByAppointmentId(Long appointmentId);

    Response<List<ConsultationDTO>> getConsultationHistoryForPatient(Long patientId);


}
