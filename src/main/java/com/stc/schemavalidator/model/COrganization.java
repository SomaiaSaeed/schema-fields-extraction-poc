package com.stc.schemavalidator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class COrganization {

    @JsonProperty("code")
    private String code;

    @JsonProperty("name")
    private String name;
}
