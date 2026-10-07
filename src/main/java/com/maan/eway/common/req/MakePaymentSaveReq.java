package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MakePaymentSaveReq {

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("EmiYn")
	private String emiYn;
	
	@JsonProperty("InstallmentMonth")
	private String installmentMonth;
	
	@JsonProperty("InstallmentPeriod")
	private String installmentPeriod;
	
	
	@JsonProperty("Premium")
	private String premium;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("UserType")
	private String userType;
	
	@JsonProperty("SubUserType")
	private String subUserType;
	
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
}
