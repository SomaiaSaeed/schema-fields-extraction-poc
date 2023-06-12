package com.stc.schemavalidator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Qualification {
    @JsonProperty("type")
    private String type;

    @JsonProperty("specialization")
    private String specialization;

}
