package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ClausesMasterSaveReq {

	@JsonProperty("ClausesId")
	private String clausesId;
	
	@JsonProperty("ClausesDescription")
	private String clausesDescription;
	
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

	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
//	
//	@JsonProperty("PolicyType")
//	private String policyType;

	@JsonProperty("DocRefNo")
	private String docRefNo;

	@JsonProperty("TypeId")
	private String typeId;
	
	@JsonProperty("BrokerCode")
	private String brokerCode;
	
	@JsonProperty("CodeDescLocal")
    private String codeDescLocal;
	
	@JsonProperty("CategoryDescription")
	private String categoryDescription;

}
