package com.maan.eway.master.res;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ErrorDescMasterRes implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@JsonProperty("ProductId")
	private String productId;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date updatedDate;

	@JsonProperty("ErrorCode")
	private String errorCode;
	
	@JsonProperty("ErrorField")
	private String errorField;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ModuleId")
	private String moduleId;

	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("ErrorDesc")
	private String errorDesc;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("UpdatedBy")
	private String updatedBy;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("AmendId")
	private String amendId;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonProperty("Language")
	private String language;
	
	@JsonProperty("LocalLangErrorField")
	private String localLangErrorField;
	
	@JsonProperty("LocalLanguageDesc")
	private String localLanguageDesc;

}
