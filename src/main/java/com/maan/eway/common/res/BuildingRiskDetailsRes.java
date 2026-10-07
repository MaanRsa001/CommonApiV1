package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.CoverRes;

import lombok.Data;

@Data
public class BuildingRiskDetailsRes {

	@JsonProperty("RiskId")
	private  String riskId;	
	
	@JsonProperty("RiskDetails")
	private  Object riskDetails   ;	
	
	@JsonProperty("SectionId")
	private  String sectionId;	
	
	@JsonProperty("SectionName")
	private  String sectionName;	
	
	
	
	
	@JsonProperty("Covers")
	private  List<CoverRes> covers ;
}
