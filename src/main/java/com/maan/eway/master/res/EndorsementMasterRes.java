package com.maan.eway.master.res;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EndorsementMasterRes {

	@JsonProperty("EndtTypeId")
	private String endtTypeId;
	
	@JsonProperty("EndtType")
	private String endtType;
	
	@JsonProperty("EndtTypeDesc")
	private String endtTypeDesc;
	
	@JsonProperty("EndtTypeCategoryId")
	private String endtTypeCategoryId;
	
	@JsonProperty("EndtTypeCategory")
	private String endtTypeCategory;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("Priority")
	private String priority;
	
	@JsonProperty("EndtDependantFields")
	private List<String> endtDependantFields;

	@JsonProperty("EndtDependantIds")
	private List<String> endtDependantIds;

	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("EndtFeeYn")
	private String endtFeeYn;
	
	@JsonProperty("EndtFeePercent")
	private String endtFeePercent;
	
	@JsonProperty("Remarks")
	private String remarks;

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;

	@JsonProperty("CreatedBy")
	private String createdBy;

	@JsonProperty("UpdatedBy")
	private String updatedBy;

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date UpdatedDate;

	@JsonProperty("AmendId")
	private String amendId;

	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
	
	@JsonProperty("CalcTypeId")
	private String calcTypeId;
	

	@JsonProperty("SectionModificationYn")
	private String sectionModificationYn;
	
	@JsonProperty("SectionModificationType")
	private String sectionModificationType;
	
	@JsonProperty("IsCoverEndorsementYN")
	private String isCoverEndorsementYN;
	
	@JsonProperty("EndtShortCode")
	private String endtShortCode;
	

	@JsonProperty("EndtShortDesc")
	private String endtShortDesc;
/*
	@JsonProperty("CalcType")
	private String calcType;
*/
}
