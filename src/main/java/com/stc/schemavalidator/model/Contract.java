package com.stc.schemavalidator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Contract {
    @JsonProperty("startDate")
    private String startDate;

    @JsonProperty("endDate")
    private String endDate;

}







