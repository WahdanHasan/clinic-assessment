package com.wahdanhasan.cxunicorn.assessment.api.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookAppointmentReqDto {

    private Integer patientId;
    private Integer doctorId;
    private String appointmentDate;
    private String appointmentTimeStart;
    private String appointmentTimeEnd;
}
