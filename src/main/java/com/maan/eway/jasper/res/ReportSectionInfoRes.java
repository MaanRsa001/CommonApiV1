package com.maan.eway.jasper.res;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.jasper.risklist.SectionDetailsDTO;
import com.maan.eway.jasper.service.impl.CollateralRes;

import lombok.Data;

@Data
public class ReportSectionInfoRes {

	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("SectionName")
	private String sectionName;
	
	@JsonProperty("SumInsured")
	private Double sumInsured;
	
	@JsonProperty("Premium")
	private Double premium;
	
	@JsonProperty("CoverDetails")
	private List<ReportCoverInfoRes> coverDetails;
	
	@JsonProperty("ExcessDetails")
	private List<ReportExcessInfoRes> excessDetails;
	
	@JsonProperty("BenefitDetails")
	private List<ReportBenefitInfoRes> benefitDetails;
	
	@JsonProperty("FirstConditionDetails")
	private List<LinkedHashMap<String, Object>> firstconditionsDetails;
	
	@JsonProperty("SecondConditionDetails")
	private List<LinkedHashMap<String, Object>> secondconditionsDetails;
	
	@JsonProperty("ClausesList")
	private List<Map<String,Object>> clausesList;
	
	@JsonProperty("EngInfoList")
	private List<Map<String,Object>> engInfoList;
	
	@JsonProperty("SectionRiskDetails")
	private SectionDetailsDTO sectionRiskDetails;
	
	@JsonProperty("CollateralValueList")
	private List<CollateralRes> collateralValueList = new ArrayList<>();


	
}
