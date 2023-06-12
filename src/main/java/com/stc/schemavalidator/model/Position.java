package com.stc.schemavalidator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Position {
    @JsonProperty("code")
    private String code;

    @JsonProperty("name")
    private String name;
}
