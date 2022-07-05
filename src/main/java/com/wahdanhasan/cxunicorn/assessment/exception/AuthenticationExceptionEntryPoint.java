package com.wahdanhasan.cxunicorn.assessment.exception;

import com.wahdanhasan.cxunicorn.assessment.api.dto.common.GenericResponseDto;
import com.wahdanhasan.cxunicorn.assessment.api.dto.common.ResponseMessage;
import com.wahdanhasan.cxunicorn.assessment.util.Constants;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/* Custom authentication entry point
*
*  Serves the purpose of ensuring the user is logged in. Throws forbidden response if not
*
*  */

@Component
public class AuthenticationExceptionEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        /* Send forbidden response if the user is not logged in */
        response.setContentType(APPLICATION_JSON_VALUE);

        ResponseMessage message = new ResponseMessage(HttpStatus.FORBIDDEN.value(), Constants.FORBIDDEN_DESC);

        response.getWriter()
                .write(new ObjectMapper().writeValueAsString(new GenericResponseDto(message)));
    }
}
