package com.stc.schemavalidator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Sector {

    @JsonProperty("code")
    private String code;

    @JsonProperty("name")
    private String name;
}
