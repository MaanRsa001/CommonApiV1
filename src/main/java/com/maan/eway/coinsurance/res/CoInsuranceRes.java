package com.maan.eway.coinsurance.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class CoInsuranceRes {

	@JsonProperty("QuoteNo")

	private String quoteNo;

	@JsonProperty("CompanyId")

	private String companyId;

	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("ProductDesc")
	private String productDesc;

	@JsonProperty("CustomerName")
	private String customerName;

	@JsonProperty("PolicyStartDate")
	private String policyStartDate;

	@JsonProperty("PolicyEndDate")
	private String policyEndDate;

	@JsonProperty("SumInsuredLc")
	private String sumInsuredLc;

	@JsonProperty("SumInsuredFc")
	private String sumInsuredFc;

	@JsonProperty("TotalPremiumLc")
	private String totalPremiumLc;

	@JsonProperty("TotalPremiumFc")
	private String totalPremiumFc;

	@JsonProperty("TaxAmountLc")
	private String taxAmountLc;

	@JsonProperty("TaxAmountFc")
	private String taxAmountFc;

	@JsonProperty("CurrencyId")
	private String currencyId;

	@JsonProperty("EntryDate")
	private String entryDate;

	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("ExchangeRate")
	private String exchangeRate;

//	@JsonProperty("Details")
//	List<CoinsuranceListRes> details;
//
//	@JsonProperty("EndorsementsDetails")
//	List<EndorsementsCoInsuranceReq> Endorsementsdetails;

}
