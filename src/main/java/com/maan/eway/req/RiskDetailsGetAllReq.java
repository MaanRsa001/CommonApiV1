package com.maan.eway.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RiskDetailsGetAllReq implements Serializable {

    private static final long serialVersionUID = 1L;

	
	@JsonProperty("InsuranceId")
    private String     insuranceId ;
	
	@JsonProperty("BranchCode")
    private String     branchCode;
    
    @JsonProperty("Limit")
    private String limit;
    
    @JsonProperty("Offset")
    private String offset;
    

}
