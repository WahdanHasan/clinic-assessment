package com.wahdanhasan.cxunicorn.assessment.api.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasicAppointmentDataDto {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer patientLookupId;
    private String doctorFullName;
    private String patientFullName;
    private String date;
    private String timeStart;
    private String timeEnd;
    private String durationMins;
    private Boolean patientAttended;
    private String appointmentStatus;
}
