package com.maan.eway.reinsurance.res;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ViewReInsuranceDetails {
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyFrom")
	private Date policyFrom;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyTo")
	private Date policyTo;
	
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("ProductName")
	private String productName;
	
	@JsonProperty("EndtFromDate")
	private Date endtFromDate;
	
	@JsonProperty("EndtToDate")
	private Date endtToDate;

	@JsonProperty("Currency")
	private String currency;
	
	@JsonProperty("RiBasisId")
	private String  riBasisId;
	
	@JsonProperty("RiBasisName")
	private String  riBasisName;
	
	@JsonProperty("FACPerc")
	private String  facPerc;
	
    @JsonProperty("CreatedBy")
    private String createdBy;
	
    @JsonProperty("RIStatus")
    private String riStatus;
    
	@JsonProperty("RiskList")
	private List<ReInsuranceRiskDetail> reInsuranceRiskDetails;
	
}
