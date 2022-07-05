package com.wahdanhasan.cxunicorn.assessment.util.mapper;

import com.wahdanhasan.cxunicorn.assessment.api.dto.user.RegisterUserReqDto;
import com.wahdanhasan.cxunicorn.assessment.db.entity.DoctorEntity;
import com.wahdanhasan.cxunicorn.assessment.db.entity.EmployeeEntity;
import com.wahdanhasan.cxunicorn.assessment.db.entity.PatientEntity;
import com.wahdanhasan.cxunicorn.assessment.db.entity.UserEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/* Used for mapping objects of one type onto another */

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "genderChar", target = "gender")
    @Mapping(source = "dateOfBirthDate", target = "dateOfBirth")
    UserEntity registerUserReqDtoToUserEntity(RegisterUserReqDto registerUserReqDto);

    @Mapping(source = "patient.bloodType", target = "bloodType")
    @Mapping(source = "dateOfBirthDate", target = "dateOfBirth")
    PatientEntity registerUserReqDtoToPatientEntity(RegisterUserReqDto registerUserReqDto);

    @Mapping(source = "employee.phoneExt", target = "phoneExt")
    @Mapping(source = "employee.officeRoomNo", target = "officeRoomNumber")
    @Mapping(source = "employee.workStartTimes", target = "workStartTimes")
    @Mapping(source = "employee.workEndTimes", target = "workEndTimes")
    @Mapping(source = "employee.specialty", target = "specialty")
    @Mapping(source = "dateOfBirthDate", target = "dateOfBirth")
    DoctorEntity registerUserReqDtoToDoctorEntity(RegisterUserReqDto registerUserReqDto);

    @Mapping(source = "employee.phoneExt", target = "phoneExt")
    @Mapping(source = "employee.officeRoomNo", target = "officeRoomNumber")
    @Mapping(source = "employee.workStartTimes", target = "workStartTimes")
    @Mapping(source = "employee.workEndTimes", target = "workEndTimes")
    @Mapping(source = "dateOfBirthDate", target = "dateOfBirth")
    EmployeeEntity registerUserReqDtoToEmployeeEntity(RegisterUserReqDto registerUserReqDto);
}
