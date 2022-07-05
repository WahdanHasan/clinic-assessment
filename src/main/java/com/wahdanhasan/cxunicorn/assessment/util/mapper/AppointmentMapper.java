package com.wahdanhasan.cxunicorn.assessment.util.mapper;

import com.wahdanhasan.cxunicorn.assessment.api.dto.appointment.BasicAppointmentDataDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.appointment.BookAppointmentReqDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.appointment.GetAppointmentByIdRespDto;
import com.wahdanhasan.cxunicorn.assessment.db.entity.AppointmentEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/* Used for mapping objects of one type onto another */

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(source = "patientId", target = "patient.id")
    @Mapping(source = "doctorId", target = "doctor.id")
    @Mapping(source = "appointmentDate", target = "dateCreated")
    @Mapping(source = "appointmentTimeStart", target = "timeStart")
    @Mapping(source = "appointmentTimeEnd", target = "timeEnd")
    AppointmentEntity bookAppointmentReqDtoToAppointmentEntity(BookAppointmentReqDto bookAppointmentReqDto);

    @Mapping(source = "dateCreated", target = "date")
    @Mapping(source = "patient.id", target = "patientId")
    GetAppointmentByIdRespDto appointmentEntityToGetAppointmentByIdRespDto(AppointmentEntity appointmentEntity);

    @Mapping(source = "dateCreated", target = "date")
    BasicAppointmentDataDto appointmentEntityToBasicAppointmentDataDto(AppointmentEntity appointment);
}
