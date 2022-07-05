package com.wahdanhasan.cxunicorn.assessment.api.service;

import com.wahdanhasan.cxunicorn.assessment.api.dto.user.RegisterUserReqDto;
import com.wahdanhasan.cxunicorn.assessment.exception.RestException;

public interface UserService {
    void createUser(RegisterUserReqDto data) throws RestException;
}
