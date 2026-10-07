package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EndtDependantFieldMasterSaveReq {

	@JsonProperty("DependantFieldId")
	private String dependantFieldId;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("DependantFieldName")
	private String dependantFieldName;

	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;

	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("Remarks")
	private String remarks;
}
