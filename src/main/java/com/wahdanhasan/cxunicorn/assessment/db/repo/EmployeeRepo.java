package com.wahdanhasan.cxunicorn.assessment.db.repo;

import com.wahdanhasan.cxunicorn.assessment.db.entity.EmployeeEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepo extends JpaRepository<EmployeeEntity, Integer> {
}
