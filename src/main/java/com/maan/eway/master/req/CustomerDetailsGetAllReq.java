package com.maan.eway.master.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CustomerDetailsGetAllReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("InsuranceId")
    private String insuranceId;
    
    @JsonProperty("CreatedBy")
    private String createdBy;
    
    @JsonProperty("Limit")
    private String limit;
    
    @JsonProperty("Offset")
    private String offset;
    

}
