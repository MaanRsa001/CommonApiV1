package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FrameOldDocSaveReq {


	@JsonProperty("Id") 
    private String     Id;
	
	@JsonProperty("RiskId") 
    private String     riskId;
	
	@JsonProperty("LocationId") 
    private String     locationId;
	
	@JsonProperty("LocationName") 
    private String     locationName;
	
	@JsonProperty("SectionId") 
    private String     sectionId;
	
	@JsonProperty("SectionName") 
    private String     sectionName;
}
