package com.wahdanhasan.cxunicorn.assessment.api.service.impl;

import com.wahdanhasan.cxunicorn.assessment.db.entity.*;
import com.wahdanhasan.cxunicorn.assessment.db.repo.*;
import com.wahdanhasan.cxunicorn.assessment.util.Utility;
import com.wahdanhasan.cxunicorn.assessment.util.mapper.UserMapper;
import com.wahdanhasan.cxunicorn.assessment.api.dto.user.RegisterUserReqDto;
import com.wahdanhasan.cxunicorn.assessment.api.service.UserService;
import com.wahdanhasan.cxunicorn.assessment.exception.RestException;
import com.wahdanhasan.cxunicorn.assessment.util.Constants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private DoctorRepo doctorRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private PatientRepo patientRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        /* Find user in DB */
        UserEntity user = userRepo.findByEmail(username);

        /* If user does not exist, throw exception */
        if (user == null){
            throw new UsernameNotFoundException("Username not found");
        }

        /* Get user roles */
        List<String> userRolePermissions = roleRepo.getUserRolePermissions(user.getId());

        /* Parse roles to authorities */
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>(userRolePermissions.size());

        userRolePermissions.forEach((rolePermission)-> {
            authorities.add(new SimpleGrantedAuthority(rolePermission));
        });

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    public void createUser(RegisterUserReqDto registerUserReqDto) throws RestException {

        /* Verify the integrity of the payload data */
        if (registerUserReqDto == null){
            throw new RestException(HttpStatus.BAD_REQUEST.value(), Constants.EMPTY_PAYLOAD);
        }

        /* Verify that the user does not already exist */
        UserEntity userEntity = userRepo.findByEmail(registerUserReqDto.getEmail());

        if (userEntity != null){
            throw new RestException(HttpStatus.BAD_REQUEST.value(), "User already exists.");
        }


        /* Verify the integrity of the roles. If they are valid, add them to the entity */
        if (registerUserReqDto.getRoles() == null || registerUserReqDto.getRoles().isEmpty()){
            throw new RestException(HttpStatus.BAD_REQUEST.value(), String.format(Constants.MISSING_FIELD, "roles"));
        }

        List<RoleEntity> roles = roleRepo.findAll();
        List <UserRolesEntity> userRoles = new ArrayList<>();
        boolean roleFound;

        for(String dtoRole : registerUserReqDto.getRoles()){
            roleFound = false;
            for (RoleEntity roleEntityTemp : roles){
                if (dtoRole.equals(roleEntityTemp.getName())){
                    roleFound = true;
                    UserRolesEntity userRolesEntityTemp = new UserRolesEntity();

                    userRolesEntityTemp.setRole(roleEntityTemp);
                    userRoles.add(userRolesEntityTemp);
                    break;
                }
            }

            /* If provided role does not exist, throw exception */
            if (!roleFound)
                throw new RestException(HttpStatus.BAD_REQUEST.value(), String.format(Constants.MALFORMED_FIELD, "role:" + dtoRole));
        }

        /* Validate the user attributes */
        Utility.validateUserFields(registerUserReqDto);

        /* Validate the user roles to ensure multiple unique roles aren't assigned */
        Utility.validateUserRolesUniqueness(registerUserReqDto.getRoles());

        /* Validate the employee attributes */
        if (registerUserReqDto.getRoles().contains(Constants.ROLE_DOCTOR) || registerUserReqDto.getRoles().contains(Constants.ROLE_CLINIC_ADMIN)) {
            Utility.validateEmployeeFields(registerUserReqDto);
        }



        /* Encrypt the password */
        registerUserReqDto.setPassword(passwordEncoder.encode(registerUserReqDto.getPassword()));

        /* Parse date */
        registerUserReqDto.setDateOfBirthDate(Utility.stringDateToLocalDate(registerUserReqDto.getDateOfBirth()));

        /* Set user roles in DTO */
        registerUserReqDto.setUserRoles(userRoles);


        /* Convert request DTO to entity based on role, then save the entity to DB */
        /* This design assumes that certain roles are unique and a user cannot have more than one */
        if (registerUserReqDto.getRoles().contains(Constants.ROLE_PATIENT)){
            PatientEntity patientEntity = userMapper.registerUserReqDtoToPatientEntity(registerUserReqDto);
            setUserEntityRoleRelationship(patientEntity);

            patientRepo.save(patientEntity);
        }
        else if (registerUserReqDto.getRoles().contains(Constants.ROLE_DOCTOR)){
            DoctorEntity doctorEntity = userMapper.registerUserReqDtoToDoctorEntity(registerUserReqDto);
            setUserEntityRoleRelationship(doctorEntity);

            doctorRepo.save(doctorEntity);
        }
        else if (registerUserReqDto.getRoles().contains(Constants.ROLE_CLINIC_ADMIN)){
            EmployeeEntity employeeEntity = userMapper.registerUserReqDtoToEmployeeEntity(registerUserReqDto);
            setUserEntityRoleRelationship(employeeEntity);

            employeeRepo.save(employeeEntity);
        }
        else{
            userEntity = userMapper.registerUserReqDtoToUserEntity(registerUserReqDto);

            userRepo.save(userEntity);
        }

    }

    private void setUserEntityRoleRelationship(UserEntity userEntity){
        if (userEntity.getUserRoles() != null){

            userEntity.getUserRoles().forEach((role)-> {
                role.setUser(userEntity);
            });
        }
    }
}
