package com.wahdanhasan.cxunicorn.assessment.db.repo;

import com.wahdanhasan.cxunicorn.assessment.db.entity.AppointmentEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepo extends JpaRepository<AppointmentEntity, Integer> {

    @Query("SELECT ae " +
            "FROM AppointmentEntity ae " +
            "WHERE ae.doctor.id=:doctorId " +
                "AND ae.dateCreated=:appointmentDate " +
                "AND ae.appointmentStatus=:status " +
                "GROUP BY ae.id " +
                "ORDER BY ae.timeStart")
    List<AppointmentEntity> getAppointmentsForDayByDoctorId(@Param("appointmentDate") LocalDate appointmentDate,
                                                            @Param("doctorId") Integer doctorId,
                                                            @Param("status") String appointmentStatus);

    @Query("SELECT ae " +
            "FROM AppointmentEntity ae " +
            "WHERE ae.patient.id=:patientId " +
            "AND ae.dateCreated=:appointmentDate " +
            "AND ae.appointmentStatus=:status")
    List<AppointmentEntity> getAppointmentsForDayByPatientId(@Param("appointmentDate") LocalDate appointmentDate,
                                                             @Param("patientId") Integer patientId,
                                                             @Param("status") String appointmentStatus);

    @Query ("SELECT ae " +
            "FROM AppointmentEntity ae " +
            "WHERE ae.patient.id=:patientId")
    Page<AppointmentEntity> getPaginatedAppointmentsByPatientId(@Param("patientId")Integer patientId,
                                                                Pageable pageable);

    @Query("SELECT ae " +
            "FROM AppointmentEntity ae " +
            "WHERE ae.dateCreated=:date " +
            "AND ae.appointmentStatus=:status")
    List<AppointmentEntity> getAppointmentsByDateWithStatus(@Param("date") LocalDate date,
                                                            @Param("status") String appointmentStatus);
}
