package com.wahdanhasan.cxunicorn.assessment.api.dto.doctor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wahdanhasan.cxunicorn.assessment.api.dto.appointment.BasicAppointmentDataDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetDoctorAvailableSlotRespDto {
    private String doctorFullName;
    private Integer doctorId;
    private List<BasicAppointmentDataDto> availableSlots;
    private List<BasicAppointmentDataDto> occupiedSlots;
}
