package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ExclusionMasterSaveReq {

	@JsonProperty("ExclusionId")
	private String exclusionId;
	
	@JsonProperty("ExclusionDescription")
	private String exclusionDescription;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
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
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;

	@JsonProperty("DocRefNo")
	private String docRefNo;

	@JsonProperty("TypeId")
	private String typeId;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
	//	
//	@JsonProperty("PolicyType")
//	private String policyType;
	
	@JsonProperty("CategoryDescription")
	private String categoryDescription;
	
	@JsonProperty("CoverId")
	private String coverId;
	
	
}
