package com.wahdanhasan.cxunicorn.assessment.db.repo;

import com.wahdanhasan.cxunicorn.assessment.db.entity.RoleEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepo extends JpaRepository<RoleEntity, Integer> {

    @Query("SELECT re " +
            "FROM RoleEntity re " +
            "WHERE re.id=:userId")
    List<RoleEntity> getRolesFromUserId(@Param("userId") Integer userId);

    @Query("SELECT re.name " +
            "FROM RoleEntity re ")
    List<String> getAllRoleNames();

    @Query("SELECT pe.name " +
            "FROM PermissionEntity pe, UserRolesEntity ure, RolePermissionsEntity rpe " +
            "WHERE pe.id=rpe.permission.id " +
            "AND rpe.role.id=ure.role.id " +
            "AND ure.user.id=:id " +
            "GROUP BY pe.name")
    List<String> getUserRolePermissions(@Param("id") Integer id);

    @Query("SELECT re.name " +
            "FROM RoleEntity re, UserRolesEntity ure, UserEntity ue " +
            "WHERE re.id=ure.role.id " +
                "AND ure.user.email=:email")
    List<String> getUserRolesFromEmail(@Param("email") String userEmail);
}
