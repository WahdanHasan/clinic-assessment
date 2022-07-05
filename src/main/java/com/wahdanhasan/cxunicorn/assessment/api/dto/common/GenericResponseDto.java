package com.wahdanhasan.cxunicorn.assessment.api.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenericResponseDto<T> implements Serializable {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    T data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    PaginationDto pagination;
    ResponseMessage message;

    public GenericResponseDto(ResponseMessage message) {
        this.message = message;
    }

    public GenericResponseDto(T data, ResponseMessage message) {
        this.data = data;
        this.message = message;
    }
}
