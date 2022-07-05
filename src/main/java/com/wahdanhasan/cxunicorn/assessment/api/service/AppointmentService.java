package com.wahdanhasan.cxunicorn.assessment.api.service;

import com.wahdanhasan.cxunicorn.assessment.api.dto.appointment.BookAppointmentReqDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.appointment.GetAllPatientAppointmentsRespDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.appointment.GetAppointmentByIdRespDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.common.PaginationDto;
import com.wahdanhasan.cxunicorn.assessment.exception.RestException;

public interface AppointmentService {
    void bookAppointment(BookAppointmentReqDto bookAppointmentReqDto) throws RestException;

    void cancelAppointment(Integer appointmentId) throws RestException;

    GetAppointmentByIdRespDto getAppointmentById(Integer appointmentId, String userEmail) throws RestException;

    GetAllPatientAppointmentsRespDto getAllPatientAppointments(Integer patientId, PaginationDto paginationDto, String userEmail) throws RestException;
}
