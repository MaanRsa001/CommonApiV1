package com.maan.eway.mtpintegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PolicyResponse {

    @JsonProperty("returnCode")
    private Integer returnCode;

    @JsonProperty("returnMessage")
    private String returnMessage;

    @JsonProperty("returnObject")
    private PolicyObject returnObject;
}
