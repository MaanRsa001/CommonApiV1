package com.maan.eway.master.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class IndustryMasterRes {

	
	@JsonProperty("CategoryId")
	private String categoryId;
	
	
	@JsonProperty("CategoryDesc")
	private String categoryDesc;
	
	@JsonProperty("IndustryId")
	private String industryId;
	
	@JsonProperty("IndustryName")
	private String industryName;

	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date updatedDate;
	
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
	
	@JsonProperty("UpdatedBy")
	private String updatedBy;
	
	@JsonProperty("AmendId")
	private String amendId;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
	
}
