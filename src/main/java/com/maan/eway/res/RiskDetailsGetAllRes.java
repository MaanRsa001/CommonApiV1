package com.maan.eway.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RiskDetailsGetAllRes {

	@JsonProperty("RiskDetails")
    private List<RiskDetailsRes>     riskDetails;
	
	

}
