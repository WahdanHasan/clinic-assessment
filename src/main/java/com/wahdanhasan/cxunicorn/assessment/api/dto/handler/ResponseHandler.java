package com.wahdanhasan.cxunicorn.assessment.api.dto.handler;

import com.wahdanhasan.cxunicorn.assessment.api.dto.common.GenericResponseDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.common.PaginationDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.common.ResponseMessage;
import com.wahdanhasan.cxunicorn.assessment.util.Constants;
import org.springframework.http.HttpStatus;

/* Handles API responses */

public class ResponseHandler {

    /* To return a success message with no body */
    public static GenericResponseDto responseSuccessful(){
        return new GenericResponseDto(new ResponseMessage(HttpStatus.OK.value(), Constants.OK_DESC));
    }

    /* To return a success message along with a body */
    public static GenericResponseDto responseSuccessful(Object response){
        return new GenericResponseDto(response, new ResponseMessage(HttpStatus.OK.value(), Constants.OK_DESC));
    }

    /* To return a success message, body, and pagination information */
    public static GenericResponseDto responseSuccessful(Object response, PaginationDto paginationDto){
        return new GenericResponseDto(response, paginationDto, new ResponseMessage(HttpStatus.OK.value(), Constants.OK_DESC));
    }

}
