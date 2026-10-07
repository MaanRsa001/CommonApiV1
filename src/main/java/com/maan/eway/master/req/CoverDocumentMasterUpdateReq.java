package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CoverDocumentMasterUpdateReq {

	@JsonProperty("DocumentId")
	private String documentId;
	
	@JsonProperty("DocumentDesc")
	private String documentDesc;	


	@JsonProperty("DocApplicableId")
	private String docApplicableId;

	@JsonProperty("DocApplicable")
	private String docApplicable;
	
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("CoverId")
	private String coverId;
	
	@JsonProperty("MandatoryStatus")
	private String mandatoryStatus;
	
	@JsonProperty("Remarks")
	private String remarks;

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;
	
	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
	@JsonProperty("AmendId")
	private String amendId;
	
	@JsonProperty("Status")
	private String status;

	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
	
	@JsonProperty("CreatedBy")
	private String createdBy;

}
