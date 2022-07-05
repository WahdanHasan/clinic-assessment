package com.wahdanhasan.cxunicorn.assessment.api.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResponseMessage {

    private Integer status;
    private String description;


}
