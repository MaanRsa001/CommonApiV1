package com.maan.eway.master.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AcExecutiveProductDropdownRes {

	@JsonProperty("AcExecutiveId")
	private String acExecutiveId;
	
	@JsonProperty("AcExecutiveName")
	private String acExecutiveName;
	
	@JsonProperty("BankCode")
	private String bankCode;
	
	@JsonProperty("BankName")
	private String bankName;
	
	
	}
