package com.stc.schemavalidator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
public class MailingAddress {
    @JsonProperty("street")
    private String street;

    @JsonProperty("province")
    private String province;

    @JsonProperty("addressLines")
    private List<String> addressLines;

    @JsonProperty("postalBoxNumbers")
    private List<String> postalBoxNumbers;

    @JsonProperty("city")
    private String city;

    @JsonProperty("postalCode")
    private String postalCode;

    @JsonProperty("country")
    private String country;

    // Getters and setters (omitted for brevity)
}
