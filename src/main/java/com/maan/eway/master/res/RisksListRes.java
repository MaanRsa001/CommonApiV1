package com.maan.eway.master.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RisksListRes {

	@JsonProperty("LocationId")
    private String     locationId     ;
    
	@JsonProperty("OwnHouseYn")
    private String     ownHouseYn     ;
	
	@JsonProperty("Remarks")
    private String     remarks;
}
