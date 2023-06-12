package com.stc.schemavalidator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Organization {
    @JsonProperty("code")
    private String code;

    @JsonProperty("name")
    private String name;
}
