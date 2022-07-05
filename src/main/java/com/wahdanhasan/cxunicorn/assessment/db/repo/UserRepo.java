package com.wahdanhasan.cxunicorn.assessment.db.repo;

import com.wahdanhasan.cxunicorn.assessment.db.entity.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepo extends JpaRepository<UserEntity, Integer> {

    @Query("SELECT ue " +
            "FROM UserEntity ue " +
            "WHERE ue.email=:email")
    UserEntity findByEmail(@Param("email") String email);

}
