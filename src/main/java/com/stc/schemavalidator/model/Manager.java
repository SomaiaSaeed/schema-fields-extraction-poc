package com.stc.schemavalidator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Manager {
    @JsonProperty("email")
    private String email;

    @JsonProperty("name")
    private String name;

}
