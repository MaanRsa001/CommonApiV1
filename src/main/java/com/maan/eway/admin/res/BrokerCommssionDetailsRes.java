package com.maan.eway.admin.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerCommssionDetailsRes {


	@JsonProperty("PolicyTypeId")
	private String policyTypeId;
	
	@JsonProperty("PolicyTypeDesc")
	private String policyTypeDesc;
	
	@JsonProperty("CommissionPercent")
	private String commissionPercent;
	
	@JsonProperty("SumInsuredStart")
	private String sumInsuredStart;
	
	@JsonProperty("SumInsuredEnd")
	private String sumInsuredEnd;
	
	@JsonProperty("CommissionVatYn")
	private String commissionVatYn;
	
	@JsonProperty("CommissionVatPercent")
	private String commissionVatPercent;
	
	
	@JsonProperty("BackDays")
	private String backDays;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
	
	@JsonProperty("SelectedYn")
	private String selectedYn;
	
}
