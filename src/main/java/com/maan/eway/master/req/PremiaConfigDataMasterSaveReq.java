package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data

public class PremiaConfigDataMasterSaveReq {
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
		
	@JsonProperty("InputColumn")
	private String inputColumn;
	
	@JsonProperty("DataTypeId")
	private String dataTypeId;
	
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

}
