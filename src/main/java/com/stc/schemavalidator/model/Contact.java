package com.stc.schemavalidator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Contact {
    @JsonProperty("mobile")
    private String mobile;

    @JsonProperty("work")
    private String work;

    @JsonProperty("home")
    private String home;

    @JsonProperty("fax")
    private String fax;

}

