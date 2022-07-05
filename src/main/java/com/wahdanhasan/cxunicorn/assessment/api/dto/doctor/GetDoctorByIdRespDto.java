package com.wahdanhasan.cxunicorn.assessment.api.dto.doctor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetDoctorByIdRespDto {

    private String firstName;
    private String lastName;
    private String nationality;
    private String dateOfBirth;
    private String gender;
    private String phoneNumber;
    private Integer phoneExtension;
    private String officeRoomNumber;
    private List<String> workSchedule;
    private String specialty;
}
