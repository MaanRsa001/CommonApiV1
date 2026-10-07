package com.maan.eway.master.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OfsGridSaveReq implements Serializable {

    private static final long serialVersionUID = 1L;
	
	@JsonProperty("CalcType")
	private String calcType;
	
	@JsonProperty("BaseRate")
	private String baseRate;
	
	@JsonProperty("SumInsuredStart")
	private String sumInsuredStart;
	
	@JsonProperty("SumInsuredEnd")
	private String sumInsuredEnd;
	
	@JsonProperty("MinimumPremium")
	private String minimumPremium;


	
	/*
	 	@JsonProperty("ToolTip")
	private String toolTip;
	
	 @JsonProperty("CoreCode")
	private String coreCode;

	@JsonProperty("RatingId")
	private String    ratingId ;

	@JsonProperty("RatingSetupName")
	private String ratingSetupName;
	
	@JsonProperty("RatingSetupShort")
	private String ratingSetupShort;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("RatingEffectDate")
	private Date ratingEffectDate;*/





}
