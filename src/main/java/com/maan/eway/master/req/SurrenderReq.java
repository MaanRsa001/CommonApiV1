package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SurrenderReq {
	
	
	@JsonProperty("Sno")
    private Integer     sno ;
	
	@JsonProperty("PolicyYear")
    private Integer     policyYear ;

	@JsonProperty("SurrenderPercentage")
	private Integer amount; //surrenderPercentage
	
	@JsonProperty("Status")
    private String status ;
	
	@JsonProperty("CalcType")
	private String calcType;

	@JsonProperty("CoreAppCode")
	private String coreAppCode;

	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
		
	@JsonProperty("Remarks")
	private String remarks;
}
