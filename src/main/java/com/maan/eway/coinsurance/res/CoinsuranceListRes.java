package com.maan.eway.coinsurance.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class CoinsuranceListRes {

	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("SNo")
	private String slNo;

	@JsonProperty("CompanyDesc")
	private String companyDesc;

	@JsonProperty("InsuranceCompanyId")
	private String insuranceCompanyId;

	@JsonProperty("InsuranceCompanyDesc")
	private String insuranceCompanyDesc;

	@JsonProperty("SharePrecentage")
	private String sharePrecentage;

	@JsonProperty("CoInsuranceRole")
	private String coInsuranceRole;

	@JsonProperty("PolicyStartDate")
	private String policyStartDate;

	@JsonProperty("PolicyEndDate")
	private String policyEndDate;
	
	@JsonProperty("SumInsuredLc")
	private String sumInsuredLc;

	@JsonProperty("SumInsuredFc")
	private String sumInsuredFc;

	@JsonProperty("PremiumLc")
	private String premiumLc;

	@JsonProperty("PremiumFc")
	private String premiumFc;

	@JsonProperty("TaxAmountLc")
	private String taxAmountLc;

	@JsonProperty("TaxAmountFc")
	private String taxAmountFc;
	
	@JsonProperty("CommissionPercentage")
	    private String commissionPercentage;
	    
	@JsonProperty("CommissionAmount")
	    private String commissionAmount;

}
