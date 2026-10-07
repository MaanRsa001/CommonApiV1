package com.maan.eway.claim;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class NetRes {


	 @JsonProperty("PolicyNo")
	 private String policyNo;
	 
	 @JsonProperty("SectionCode")
	 private String sectionCode;
	 
	 @JsonProperty("CoverId")
	 private String CoverId;
	 
	 @JsonProperty("CompanyId")
	 private String companyId;
	 
	 @JsonProperty("VechileId")
	 private String VechileId;
	 
	 @JsonProperty("SumInsured")
	 private String SumInsured;
	 
	 @JsonProperty("Error")
	 private String error;


}
