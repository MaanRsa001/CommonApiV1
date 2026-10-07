package com.maan.eway.common.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.common.res.EwayFactorResultRes;
import com.maan.eway.common.res.FdFactorCalcRes;

import lombok.Data;

@Data
public class FactorRateDetailsList {
	

	
	@JsonProperty("FactorCalculationRes")
	private List<FdFactorCalcRes> FactorCalculationRes;
	
	@JsonProperty("FactorResultRes")
	private EwayFactorResultRes factorResultRes;
	

	
}
