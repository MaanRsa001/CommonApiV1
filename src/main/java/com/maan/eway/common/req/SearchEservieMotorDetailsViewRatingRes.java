package com.maan.eway.common.req;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.common.res.SearchCoverDetails;

import lombok.Data;

@Data
public class SearchEservieMotorDetailsViewRatingRes {

	@JsonProperty("VehicleId")
	private String vehicleId ;
	
	 @JsonProperty("SectionName") 
	 private String sectionName;
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;

	@JsonProperty("ActualPremiumLc")
	private String actualPremiumLc;
	
	@JsonProperty("AcctualPremiumFc")
	private String actualPremiumFc ;
	
	@JsonProperty("OverallPremiumLc")
	private String overallPremiumLc ;
	
	@JsonProperty("OverallPremiumFc")
	private String    overallPremiumFc ;
		
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("PolicyStartDate")
    private Date policyStartDate;

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("PolicyEndDate")
    private Date policyEndDate;
	
	@JsonProperty("CoverList")
	private List<SearchCoverDetails> coverList ;
	
    
    @JsonProperty("ExchangeRate")
    private BigDecimal exchangeRate;
    @JsonProperty("Currency")
    private String currency;
    


}
