package com.maan.eway.master.req;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ExclusionMasterListSaveReq {

	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
//	@JsonProperty("UpdatedBy")
//	private String updatedBy;
//	
//	@JsonFormat(pattern="dd/MM/yyyy")
//	@JsonProperty("UpdatedDate")
//	private Date updatedDate;

	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
	

	@JsonProperty("DocRefNo")
	private String docRefNo;


	@JsonProperty("ExclusionMasterReq")
	private List<ExclusionMasterReq> exclusionReq;

	
	
	//	
//	@JsonProperty("PolicyType")
//	private String policyType;
	
	
}
