package com.wahdanhasan.cxunicorn.assessment.api.service;

import com.wahdanhasan.cxunicorn.assessment.api.dto.common.PaginationDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.doctor.GetAllDoctorsRespDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.doctor.GetBusyDoctorsRespDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.doctor.GetDoctorAvailableSlotRespDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.doctor.GetDoctorByIdRespDto;
import com.wahdanhasan.cxunicorn.assessment.exception.RestException;

import java.util.List;

public interface DoctorService {
    List<GetAllDoctorsRespDto> getAllDoctors() throws RestException;

    GetDoctorByIdRespDto getDoctorById(Integer doctorId) throws RestException;

    List<GetBusyDoctorsRespDto> getBusyDoctorsByAppointmentCount(String date) throws RestException;

    List<GetBusyDoctorsRespDto> getBusyDoctorsByAppointmentHours(Integer minimumHours, String date) throws RestException;

    GetDoctorAvailableSlotRespDto getAvailableSlotsByDoctorId(Integer doctorId, String date, String userEmail) throws RestException;

    List<GetDoctorAvailableSlotRespDto> getAllDoctorAvailableSlots(String date, String userEmail) throws RestException;
}
