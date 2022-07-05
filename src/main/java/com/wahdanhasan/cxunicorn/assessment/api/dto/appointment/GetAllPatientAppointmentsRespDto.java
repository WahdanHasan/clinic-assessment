package com.wahdanhasan.cxunicorn.assessment.api.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAllPatientAppointmentsRespDto {

    private String patientFullName;
    private Integer patientId;
    private List<BasicAppointmentDataDto> appointments;
}
