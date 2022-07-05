package com.wahdanhasan.cxunicorn.assessment.exception;

import com.wahdanhasan.cxunicorn.assessment.api.dto.common.GenericResponseDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.common.ResponseMessage;
import com.wahdanhasan.cxunicorn.assessment.util.Constants;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/* Custom authorization handler
 *
 *  Serves the purpose of ensuring the user is authorized to access a specific resource. Throws forbidden exception
 *  if he isn't
 *
 *  */


@Component
public class CustomAccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        /* Send forbidden response if the user does not have access to the resource */
        response.setContentType(APPLICATION_JSON_VALUE);

        ResponseMessage message = new ResponseMessage(HttpStatus.FORBIDDEN.value(), Constants.FORBIDDEN_DESC);

        response.getWriter()
                .write(new ObjectMapper().writeValueAsString(new GenericResponseDto(message)));
    }
}
