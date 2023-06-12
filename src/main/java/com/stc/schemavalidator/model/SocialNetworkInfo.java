package com.stc.schemavalidator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SocialNetworkInfo {
    @JsonProperty("facebook")
    private String facebook;

    @JsonProperty("twitter")
    private String twitter;

    @JsonProperty("linkedIn")
    private String linkedIn;

}
