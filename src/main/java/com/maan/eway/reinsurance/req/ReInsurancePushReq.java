package com.maan.eway.reinsurance.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReInsurancePushReq {

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("UwSysId")
	private String uwSysId;
	
	@JsonProperty("AhPolIdx")
	private String ahPolIdx;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("CompanyCode")
	private String companyCode;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("ProductName")
	private String productName;
	
	@JsonProperty("ProductCode")
	private String productCode;
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonProperty("RiskDetails")
	private List<ReInsuranceRisksDetails> riskList;
	
	@JsonProperty("SectionDetails")
	private List<ReInsuranceSectionDetails> sectionList;
	
	@JsonProperty("DiscLoadList")
	private List<ReInsuranceDisLoadDetails> discLoadList;
}
