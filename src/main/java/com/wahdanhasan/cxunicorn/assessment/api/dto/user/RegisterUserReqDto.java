package com.wahdanhasan.cxunicorn.assessment.api.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahdanhasan.cxunicorn.assessment.db.entity.UserRolesEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RegisterUserReqDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String dateOfBirth;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate dateOfBirthDate;
    private String nationality;
    private String gender;
    private Integer phoneCountryCode;
    private Integer phoneNumber;
    private List<String> roles;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Character genderChar;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<UserRolesEntity> userRoles;
    private EmployeeDetails employee;
    private PatientDetails patient;
}
