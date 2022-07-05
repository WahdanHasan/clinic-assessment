package com.wahdanhasan.cxunicorn.assessment.api.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wahdanhasan.cxunicorn.assessment.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginationDto {

    private int pageNum = Constants.DEFAULT_PAGE_NUMBER;
    private int pageSize = Constants.DEFAULT_PAGE_SIZE;
    private int totalPages;
    private long totalElements;
    private String sortOrder;
    private String sortBy;
}
