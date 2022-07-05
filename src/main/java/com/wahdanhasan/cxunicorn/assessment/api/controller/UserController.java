package com.wahdanhasan.cxunicorn.assessment.api.controller;

import com.wahdanhasan.cxunicorn.assessment.api.dto.common.GenericResponseDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.handler.ResponseHandler;
import com.wahdanhasan.cxunicorn.assessment.api.dto.user.RegisterUserReqDto;
import com.wahdanhasan.cxunicorn.assessment.api.service.impl.UserServiceImpl;
import com.wahdanhasan.cxunicorn.assessment.db.entity.UserEntity;
import com.wahdanhasan.cxunicorn.assessment.db.repo.UserRepo;
import com.wahdanhasan.cxunicorn.assessment.exception.RestException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("")
public class UserController {

    @Autowired
    UserServiceImpl userService;

    @Autowired
    UserRepo userRepo;

    /* Register user */
    @PutMapping("/register")
    public GenericResponseDto<?> createUser(@RequestBody(required = false) RegisterUserReqDto registerUserReqDto) throws RestException {

        userService.createUser(registerUserReqDto);

        return ResponseHandler.responseSuccessful();
    }

    /* Log in user is built into the spring boot framework. I've overridden its authentication/authorization.
    *  You will find the responsible classes/functions in the security package and in UserServicesImpl.java
    *  */


    /* Unfortunately due to a bug that exists in the spring implementation of GraphQL, GraphQL cannot be implemented
    *  into this application. This issue exists due to the co-existence of request mappings.
    *  I intended to implement GraphQL with a new API requirement of 'Get User' to showcase it.
    *
    *  More information about the issue can be found here: https://github.com/spring-projects/spring-graphql/issues/404
    * */
    @SchemaMapping(typeName = "Query", value = "allUsers")
    public List<UserEntity> getUsers() {
        return userRepo.findAll();
    }

}
