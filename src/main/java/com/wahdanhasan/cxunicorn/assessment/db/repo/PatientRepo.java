package com.wahdanhasan.cxunicorn.assessment.db.repo;

import com.wahdanhasan.cxunicorn.assessment.db.entity.PatientEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepo extends JpaRepository<PatientEntity, Integer> {

    @Query("SELECT pe " +
            "FROM PatientEntity pe " +
            "WHERE pe.id IN (:patientIds)")
    List<PatientEntity> getAllByIdList(@Param("patientIds") List<Integer> patientIdList);
}
