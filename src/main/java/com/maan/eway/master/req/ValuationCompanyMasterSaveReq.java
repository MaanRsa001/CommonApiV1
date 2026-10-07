package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ValuationCompanyMasterSaveReq {

	@JsonProperty("Sno")
	private String sNo;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("ValCompanyCode")
	private String valCompanyCode;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	
	@JsonProperty("ValCompanyName")	
	private String valCompanyName;
	
	@JsonProperty("AuthApi")
	private String authApi;
	
	@JsonProperty("AuthUserName")
	private String authUserName;
	
	@JsonProperty("AuthPassword")
	private String authPassword;

	@JsonProperty("CreateApi")
	private String createApi;
	
	@JsonProperty("StatusApi")
	private String statusApi;
	
	@JsonProperty("GetDetailApi")
	private String getDetailApi;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
//	@JsonFormat(pattern="dd/MM/yyyy")
//	@JsonProperty("EffectiveDateEnd")
//	private Date effectiveDateEnd;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
//	@JsonProperty("UpdatedBy")
//	private String updatedBy;
	
//	@JsonFormat(pattern="dd/MM/yyyy")
//	@JsonProperty("UpdatedDate")
//	private Date updatedDate;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonProperty("Remarks")
	private String remarks;
}
