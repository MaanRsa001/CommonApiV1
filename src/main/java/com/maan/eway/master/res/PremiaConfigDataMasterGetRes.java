package com.maan.eway.master.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data

public class PremiaConfigDataMasterGetRes {
	@JsonProperty("PremiaId")
	private String premiaId;

	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("InputColumn")
	private String inputColumn;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonProperty("ColumnId")
	private String columnId;
	
	@JsonProperty("ColumnName")
	private String columnName;

	@JsonProperty("DefaultYn")
	private String defaultYn;

	@JsonProperty("DefaultValue")
	private String defaultValue;
	
	
	@JsonProperty("DataTypeId")
	private String dataTypeId;

	@JsonProperty("DataTypeDesc")
	private String dataTypeDesc;

	
	@JsonProperty("DateFormatType")
	private String dateFormatType;
	
	@JsonProperty("CaseConditionYn")
	private String caseConditionYn;
	
	@JsonProperty("CaseCondition")
	private String caseCondition;

	@JsonProperty("Remarks")
	private String remarks;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("AmendId")
	private String amendId;

	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;


	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date updatedDate;

	@JsonProperty("UpdatedBy")
	private String updatedBy;

}
