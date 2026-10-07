package com.maan.eway.master.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data

public class ClausesMasterRes {

	@JsonProperty("ClausesId")
	private String clausesId;
	
	@JsonProperty("ClausesDescription")
	private String clausesDescription;
	/*
	@JsonProperty("CoverId")
	private String coverId;
	
	@JsonProperty("ExtraCoverId")
	private String extraCoverId;
	
	@JsonProperty("DisplayOrder")
	private String displayOrder;
	
	@JsonProperty("PdfLocation")
	private String pdfLocation;
	
	@JsonProperty("OptionalType")
	private String optionalType;
	
	@JsonProperty("IntCode")
	private String intCode;
	*/
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("UpdatedBy")
	private String updatedBy;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date updatedDate;

	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;

	@JsonProperty("DocRefNo")
	private String docRefNo;

	@JsonProperty("TypeId")
	private String typeId;
	
	@JsonProperty("TypeDesc")
	private String typeDesc;
	
	@JsonProperty("BrokerCode")
	private String brokerCode;
	
	//	
//	@JsonProperty("PolicyType")
//	private String policyType;
	@JsonProperty("CodeDescLocal")
    private String codeDescLocal;
	
	@JsonProperty("CategoryDescription")
	private String categoryDescription;
	
	@JsonProperty("PdfName")
	private String pdfName;
	
	@JsonProperty("PdfLocation")
	private String pdfLocation;

}
