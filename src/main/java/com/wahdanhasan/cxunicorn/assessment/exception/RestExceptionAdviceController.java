package com.wahdanhasan.cxunicorn.assessment.exception;

import com.wahdanhasan.cxunicorn.assessment.api.dto.common.GenericResponseDto;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/* Advice controller for catching exceptions thrown by the APIs */

@ControllerAdvice
public class RestExceptionAdviceController {

    @ExceptionHandler(RestException.class)
    @ResponseBody
    public GenericResponseDto handleRestException(RestException re){
        GenericResponseDto genericResponseDto = new GenericResponseDto(re.getResponseMessage());

        return genericResponseDto;
    }


}
