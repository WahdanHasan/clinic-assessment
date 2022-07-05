package com.wahdanhasan.cxunicorn.assessment.util.mapper;

import com.wahdanhasan.cxunicorn.assessment.api.dto.doctor.GetAllDoctorsRespDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.doctor.GetDoctorByIdRespDto;
import com.wahdanhasan.cxunicorn.assessment.db.entity.DoctorEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/* Used for mapping objects of one type onto another */

@Mapper(componentModel = "spring")
public interface DoctorMapper {
    @Mapping(source = "phoneExt", target = "phoneExtension")
    GetAllDoctorsRespDto doctorEntityToGetAllDoctorsRespDto(DoctorEntity doctorEntity);

    @Mapping(source = "phoneExt", target = "phoneExtension")
    GetDoctorByIdRespDto doctorEntityToGetDoctorByIdRespDto(DoctorEntity doctorEntity);
}
