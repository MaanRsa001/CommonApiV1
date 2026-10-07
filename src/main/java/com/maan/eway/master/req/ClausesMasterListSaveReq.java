package com.maan.eway.master.req;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ClausesMasterListSaveReq {

	
	@JsonProperty("DisplayOrder")
	private String displayOrder;
	
	@JsonProperty("PdfLocation")
	private String pdfLocation;
	
	@JsonProperty("OptionalType")
	private String optionalType;
	
	@JsonProperty("IntCode")
	private String intCode;
	
	
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
	
//	
//	@JsonProperty("PolicyType")
//	private String policyType;

	@JsonProperty("DocRefNo")
	private String docRefNo;

	
	@JsonProperty("ClausesMasterReq")
	private List<ClausesMasterReq> clausesMasterReq;

	
}
