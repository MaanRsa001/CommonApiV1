package com.maan.eway.master.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductGroupMasterDropDownRes {

	@JsonProperty("Code")
	private String code;
	@JsonProperty("CodeDesc")
	private String codeDesc;
	@JsonProperty("Start")
	private String start;
	@JsonProperty("End")
	private String end;
	@JsonProperty("Status")
	private String status;
	
}
