package com.maan.eway.common.res;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TermsAndConditionGetRes {



	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("InsuranceName")
	private String companyName;
		
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("BranchName")
	private String branchName;
		
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("ProductName")
	private String productName;
	
	@JsonProperty("SectionId")
	private String sectionId;

	@JsonProperty("SectionName")
	private String sectionName;

	
	@JsonProperty("QuoteNo")
	private String quoteNo;

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;

	@JsonProperty("RiskId")
	private String riskId;

	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("Id")
	private String id;

	@JsonProperty("IdDesc")
	private String idDesc;

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	
	@JsonProperty("AmendId")
	private String amendId;
	
	@JsonProperty("UpdatedBy")
	private String updatedBy;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date updatedDate;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("TermsAndConditionListRes")
	private List<TermsAndConditionListRes> termsAndConditionlistRes;
	
	

}
