package com.wahdanhasan.cxunicorn.assessment.api.dto.handler;

import com.wahdanhasan.cxunicorn.assessment.api.dto.common.PaginationDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/* Generic class to map requests with data and pagination information to
*
*  */
@Getter
@Setter
@AllArgsConstructor
public class RequestHandler<T> {

    private T data;
    private PaginationDto paginationDto;
}
