package com.wahdanhasan.cxunicorn.assessment.api.dto.doctor;

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
public class GetBusyDoctorsRespDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String totalAppointmentTime;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long totalAppointmentTimeLong;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long appointmentCount;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer id;
    private String firstName;
    private String lastName;
    private Integer phoneExtension;
    private String specialty;

    public GetBusyDoctorsRespDto(Long appointmentCount, Integer id, String firstName, String lastName, Integer phoneExtension, String specialty) {
        this.appointmentCount = appointmentCount;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneExtension = phoneExtension;
        this.specialty = specialty;
    }

    public GetBusyDoctorsRespDto(Integer appointmentCount, Integer id, String firstName, String lastName, Integer phoneExtension, String specialty) {
        this.appointmentCount = Long.valueOf(appointmentCount);
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneExtension = phoneExtension;
        this.specialty = specialty;
    }

    public GetBusyDoctorsRespDto(Integer id, String firstName, String lastName, Integer phoneExtension, String specialty, Long totalAppointmentTimeLong) {
        this.totalAppointmentTimeLong = (totalAppointmentTimeLong == null)? 0L:totalAppointmentTimeLong;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneExtension = phoneExtension;
        this.specialty = specialty;
    }

    public GetBusyDoctorsRespDto(Integer id, String firstName, String lastName, Integer phoneExtension, String specialty, Integer totalAppointmentTime) {
        this.totalAppointmentTimeLong = (totalAppointmentTimeLong == null)? 0L:Long.valueOf(totalAppointmentTime);
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneExtension = phoneExtension;
        this.specialty = specialty;
    }

}
