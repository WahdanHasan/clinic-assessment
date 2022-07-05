package com.wahdanhasan.cxunicorn.assessment.api.controller;

import com.wahdanhasan.cxunicorn.assessment.api.dto.appointment.BookAppointmentReqDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.appointment.GetAllPatientAppointmentsRespDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.appointment.GetAppointmentByIdRespDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.common.GenericResponseDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.common.PaginationDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.handler.RequestHandler;
import com.wahdanhasan.cxunicorn.assessment.api.dto.handler.ResponseHandler;
import com.wahdanhasan.cxunicorn.assessment.api.service.AppointmentService;
import com.wahdanhasan.cxunicorn.assessment.exception.RestException;
import com.wahdanhasan.cxunicorn.assessment.util.Utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    /* Book an appointment with a doctor */
    @PutMapping("/book")
    @PreAuthorize("hasAuthority('appointment-create')")
    public GenericResponseDto<?> bookAppointment(@RequestBody(required = true) RequestHandler<BookAppointmentReqDto> request) throws RestException {

        appointmentService.bookAppointment(request.getData());

        return ResponseHandler.responseSuccessful();
    }

    /* Cancel an appointment */
    @PostMapping("/{appointment-id}/cancel")
    @PreAuthorize("hasAuthority('appointment-cancel')")
    public GenericResponseDto<?> cancelAppointment(@PathVariable("appointment-id") Integer appointmentId) throws RestException {

        appointmentService.cancelAppointment(appointmentId);

        return ResponseHandler.responseSuccessful();
    }

    /* View appointment details */
    @GetMapping("/{appointment-id}/details")
    @PreAuthorize("hasAuthority('view-appointment-details')")
    public GenericResponseDto<GetAppointmentByIdRespDto> getAppointmentById(HttpServletRequest request,
                                                                            @PathVariable("appointment-id") Integer appointmentId)
            throws RestException {

        GetAppointmentByIdRespDto getAppointmentByIdRespDto = appointmentService
                .getAppointmentById(appointmentId, Utility.getDecodedJWT(request).getSubject());

        return ResponseHandler.responseSuccessful(getAppointmentByIdRespDto);
    }

    /* View patient appointment history */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('view-patient-appointment-history')")
    public GenericResponseDto<GetAllPatientAppointmentsRespDto> getAllPatientAppointments(
            HttpServletRequest httpRequest,
            @RequestBody(required = false) RequestHandler<?> request,
            @RequestParam("patient_id") Integer patientId)
            throws RestException {


        PaginationDto paginationDto = Utility.getPaginationDto(request);

        GetAllPatientAppointmentsRespDto getAllPatientAppointmentsRespDto = appointmentService
                .getAllPatientAppointments(
                        patientId,
                        paginationDto,
                        Utility.getDecodedJWT(httpRequest).getSubject()
                );

        return ResponseHandler.responseSuccessful(getAllPatientAppointmentsRespDto, paginationDto);
    }
}
