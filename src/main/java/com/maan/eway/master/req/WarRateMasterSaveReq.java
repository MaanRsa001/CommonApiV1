package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class WarRateMasterSaveReq {

	@JsonProperty("WarRateId")
	private String warRateId;
	
	@JsonProperty("WarRateDesc")
	private String warRateDesc;
	
	@JsonProperty("WarRate")
	private String warRate;
	
	@JsonProperty("ModeTransportId")
	private String modeTransportId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
//	@JsonFormat(pattern="dd/MM/yyyy")
//	@JsonProperty("EffectiveDateEnd")
//	private Date effectiveDateEnd;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
//	
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
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("DocRefNo")
	private String docRefNo;
	
}
