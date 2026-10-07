package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DocumentMasterSaveReq {

	@JsonProperty("DocumentId")
	private String documentId;

	@JsonProperty("DocumentName")
	private String documentName;
	
	@JsonProperty("DocumentDesc")
	private String documentDesc;

	@JsonProperty("DocApplicableId")
	private String docApplicableId;

	@JsonProperty("MandatoryStatus")
	private String mandatoryStatus;

	@JsonProperty("Remarks")
	private String remarks;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;

	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;

	@JsonProperty("CreatedBy")
	private String createdBy;

	@JsonProperty("Status")
	private String status;


	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;

}
