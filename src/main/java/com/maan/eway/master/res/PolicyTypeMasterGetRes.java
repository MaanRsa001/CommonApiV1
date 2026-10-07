package com.maan.eway.master.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PolicyTypeMasterGetRes {

	@JsonProperty("PolicyTypeId")
	private String policyTypeId;
	
	@JsonProperty("PolicyTypeName")
	private String policyTypeName;
	 
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	 
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;
	 
	@JsonProperty("Remarks")
	private String remarks;

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonProperty("AmendId")
	private Integer amendId;
	
	@JsonProperty("Status")
	private String status;


}
