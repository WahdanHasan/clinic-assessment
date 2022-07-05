package com.wahdanhasan.cxunicorn.assessment.exception;

import com.wahdanhasan.cxunicorn.assessment.api.dto.common.ResponseMessage;

import lombok.Getter;
import lombok.Setter;

/* Serves as the general exception thrown by the REST services */
@Getter
@Setter
public class RestException extends Exception{
    private ResponseMessage responseMessage;

    public RestException(Integer errorCode, String description) {
        super(description);
        this.responseMessage = new ResponseMessage(errorCode, description);
    }


}
