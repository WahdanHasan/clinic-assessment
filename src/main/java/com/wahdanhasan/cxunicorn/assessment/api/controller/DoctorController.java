package com.wahdanhasan.cxunicorn.assessment.api.controller;

import com.wahdanhasan.cxunicorn.assessment.api.dto.common.GenericResponseDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.common.PaginationDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.doctor.GetAllDoctorsRespDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.doctor.GetBusyDoctorsRespDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.doctor.GetDoctorAvailableSlotRespDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.doctor.GetDoctorByIdRespDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.handler.ResponseHandler;
import com.wahdanhasan.cxunicorn.assessment.api.service.DoctorService;
import com.wahdanhasan.cxunicorn.assessment.exception.RestException;
import com.wahdanhasan.cxunicorn.assessment.util.Constants;
import com.wahdanhasan.cxunicorn.assessment.util.Utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    @Autowired
    DoctorService doctorService;

    /* View list of doctors */
    @GetMapping("")
    public GenericResponseDto<List<GetAllDoctorsRespDto>> getAllDoctors() throws RestException {

        List<GetAllDoctorsRespDto> doctors = doctorService.getAllDoctors();

        return ResponseHandler.responseSuccessful(doctors);
    }

    /* View doctor information */
    @GetMapping("/{id}")
    public GenericResponseDto<GetDoctorByIdRespDto> getDoctorById(@PathVariable("id") Integer doctorId) throws RestException {

        GetDoctorByIdRespDto getDoctorByIdRespDto = doctorService.getDoctorById(doctorId);

        return ResponseHandler.responseSuccessful(getDoctorByIdRespDto);
    }

    /* View doctors available slots */
    @GetMapping("/{id}/slots")
    @PreAuthorize("hasAuthority('view-doctor-available-slots')")
    public GenericResponseDto<GetDoctorAvailableSlotRespDto> getAvailableSlotsByDoctorId(HttpServletRequest request,
                                                                                         @PathVariable("id") Integer doctorId,
                                                                                         @RequestParam("date") String date
    ) throws RestException {

        GetDoctorAvailableSlotRespDto schedule = doctorService
                .getAvailableSlotsByDoctorId(doctorId, date, Utility.getDecodedJWT(request).getSubject());

        return ResponseHandler.responseSuccessful(schedule);
    }

    /* view availability of all doctors */
    @GetMapping("/all/slots")
    @PreAuthorize("hasAuthority('view-all-doctors-available-slots')")
    public GenericResponseDto<List<GetDoctorAvailableSlotRespDto>> getAllDoctorAvailabilities(HttpServletRequest request,
                                                                                              @RequestParam("date") String date) throws RestException {

        List<GetDoctorAvailableSlotRespDto> schedule = doctorService
                .getAllDoctorAvailableSlots(date, Utility.getDecodedJWT(request).getSubject());

        return ResponseHandler.responseSuccessful(schedule);
    }

    /* View doctors with the most appointments in a given day */
    @GetMapping("/busy/{date}")
    @PreAuthorize("hasAuthority('view-all-doctors-busy-by-date')")
    public GenericResponseDto<GetBusyDoctorsRespDto> getBusyDoctorsByAppointments(@PathVariable(value = "date") String date
    ) throws RestException {

        List<GetBusyDoctorsRespDto> getBusyDoctorsRespDtoList = doctorService.getBusyDoctorsByAppointmentCount(date);

        return ResponseHandler.responseSuccessful(getBusyDoctorsRespDtoList);
    }

    /* View doctors who have n+ hours total appointments in a day */
    @GetMapping("/busy/{date}/{minimum-hours}")
    @PreAuthorize("hasAuthority('view-all-doctors-busy-by-hours')")
    public GenericResponseDto<GetBusyDoctorsRespDto> getBusyDoctorsByHours(@PathVariable(value = "date") String date,
                                                                           @PathVariable(value = "minimum-hours") Integer minimumHours

    ) throws RestException {

        List<GetBusyDoctorsRespDto> getBusyDoctorsRespDtoList = doctorService.getBusyDoctorsByAppointmentHours(minimumHours, date);

        return ResponseHandler.responseSuccessful(getBusyDoctorsRespDtoList);
    }
}
