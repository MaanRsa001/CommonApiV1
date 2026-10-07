package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LovGetReq {


	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ItemType")
	private String itemType;
	
	@JsonProperty("ItemCode")
	private String itemCode;
	
	@JsonProperty("TitleType")
	private String titletype;
	
	@JsonProperty("Param1")
	private String param1;
}
