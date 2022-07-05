package com.wahdanhasan.cxunicorn.assessment.api.dto.doctor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAllDoctorsRespDto {

    private String firstName;
    private String lastName;
    private String nationality;
    private String gender;
    private Integer phoneExtension;
    private String officeRoomNumber;
    private String specialty;
}
