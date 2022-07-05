package com.wahdanhasan.cxunicorn.assessment.api.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAppointmentByIdRespDto extends BasicAppointmentDataDto {

    private String patientFullName;
    private String patientId;
}
