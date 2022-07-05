package com.wahdanhasan.cxunicorn.assessment.api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDetails {
    private Integer phoneExt;
    private String officeRoomNo;
    private String[] workStartTimes;
    private String[] workEndTimes;
    private String specialty;

}
