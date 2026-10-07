package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ErrorDescMasterSaveReq implements Serializable{
	
    private static final long serialVersionUID = 1L;

	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ModuleId")
	private String moduleId;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
//	@JsonFormat(pattern = "dd/MM/yyyy")
//	@JsonProperty("UpdatedDate")
//	private Date updatedDate;

	@JsonProperty("ErrorCode")
	private String errorCode;
	
	@JsonProperty("ErrorField")
	private String errorField;

	@JsonProperty("Status")
	private String status;

	
	@JsonProperty("ErrorDesc")
	private String errorDesc;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	
//	@JsonProperty("UpdatedBy")
//	private String updatedBy;

	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("Language")
	private String language;
	
	@JsonProperty("LocalLangErrorField")
	private String localLanguageErrField;

	@JsonProperty("LocalLanguageDesc")
	private String localLanguageErrDesc;

}
