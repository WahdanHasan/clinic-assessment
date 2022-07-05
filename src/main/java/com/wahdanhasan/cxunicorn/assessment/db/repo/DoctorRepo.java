package com.wahdanhasan.cxunicorn.assessment.db.repo;

import com.wahdanhasan.cxunicorn.assessment.api.dto.doctor.GetBusyDoctorsRespDto;
import com.wahdanhasan.cxunicorn.assessment.db.entity.DoctorEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DoctorRepo extends JpaRepository<DoctorEntity, Integer> {

    @Query("SELECT de " +
            "FROM DoctorEntity de " +
            "WHERE de.id IN (:doctorIds)")
    List<DoctorEntity> getDoctorsById(@Param("doctorIds") List<Integer> doctorIds);

    @Query("SELECT NEW com.wahdanhasan.cxunicorn.assessment.api.dto.doctor.GetBusyDoctorsRespDto(" +
                    "(SELECT COUNT(ae.id) " +
                    "FROM AppointmentEntity ae " +
                    "WHERE ae.doctor.id=de.id " +
                    "AND ae.dateCreated=:date" +
            "), " +
            "de.id, de.firstName, de.lastName, de.phoneExt, de.specialty) " +
            "FROM DoctorEntity de ")
    List<GetBusyDoctorsRespDto> getAllDoctorsByAppointmentCount(@Param("date") LocalDate date);

    @Query("SELECT NEW com.wahdanhasan.cxunicorn.assessment.api.dto.doctor.GetBusyDoctorsRespDto(" +
            "de.id, de.firstName, de.lastName, de.phoneExt, de.specialty, " +
                    "(SELECT SUM(ae.durationMins) " +
                    "FROM AppointmentEntity ae " +
                    "WHERE ae.doctor.id=de.id " +
                    "AND ae.dateCreated=:date)" +
            ") " +
            "FROM DoctorEntity de ")
    List<GetBusyDoctorsRespDto> getAllDoctorsByAppointmentHours(@Param("date") LocalDate date);
}
