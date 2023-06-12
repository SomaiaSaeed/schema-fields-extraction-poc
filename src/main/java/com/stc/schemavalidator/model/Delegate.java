package com.stc.schemavalidator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Delegate {

    @JsonProperty("email")
    private String email;

    @JsonProperty("name")
    private String name;
}
