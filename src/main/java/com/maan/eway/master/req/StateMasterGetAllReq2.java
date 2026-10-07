package com.maan.eway.master.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class StateMasterGetAllReq2 implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("Limit")
    private String limit;
    
    @JsonProperty("Offset")
    private String offset;
   

    @JsonProperty("CountryId")
    private Integer countryId;
    

    @JsonProperty("RegionCode")
    private Integer regionCode;
}
