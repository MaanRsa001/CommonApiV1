package com.maan.eway.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CommonSumInsuredDetails {

	@JsonProperty("RiskId")
	private String riskId;
	
	@JsonProperty("SumInsured")
    private String     sumInsured ;
	
	@JsonProperty("EmpLiabilitySi")
    private String     empLiabilitySi ;
	
	@JsonProperty("FidEmpSi")
    private String     fidEmpSi;
	
	@JsonProperty("LiabilitySi")
    private String     liabilitySi;
	
	@JsonProperty("CurrencyId")
    private String   currencyId ;
	
	@JsonProperty("Count")
    private String    count;
	
	@JsonProperty("OccupationType")
    private String    occupationType;
	
	@JsonProperty("OccupationTypeDesc")
    private String    occupationTypeDesc;

	@JsonProperty("LiabilityOccupationId") 
    private String     liabilityOccupationId ;
   
   @JsonProperty("LiabilityOccupationDesc") 
    private String     liabilityOccupationDesc;
   
	@JsonProperty("PersonalAccSuminsured")
    private String    personalAccSuminsured;
	
	@JsonProperty("PersonalIntermediarySuminsured")
    private String     personalIntermediarySuminsured ;
	
	@JsonProperty("SectionId")
	private List<String> sectionId;
}
