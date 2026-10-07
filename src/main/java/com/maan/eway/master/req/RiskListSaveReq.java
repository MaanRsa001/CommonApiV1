package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RiskListSaveReq {
	
	@JsonProperty("LocationId")
    private String     locationId     ;
    
	@JsonProperty("OwnHouseYn")
    private String     ownHouseYn     ;
	
	@JsonProperty("Remarks")
    private String     remarks;
    
	
}
