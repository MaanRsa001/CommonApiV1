package com.maan.eway.master.req;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EndorsementMasterSaveReq {

	@JsonProperty("EndtTypeId")
	private String endtTypeId;
	
	@JsonProperty("EndtType")
	private String endtType;
	
	@JsonProperty("EndtTypeDesc")
	private String endtTypeDesc;
	
	@JsonProperty("EndtTypeCategoryId")
	private String endtTypeCategoryId;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("Priority")
	private String priority;
	
	@JsonProperty("EndtDependantIds")
	private List<String> endtDependantIds;
	
	@JsonProperty("ProductId")
	private String productId;
//	
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
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("CalcTypeId")
	private String calcTypeId;
//
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
	
	@JsonProperty("SectionModificationYn")
	private String sectionModificationYn;
	
	@JsonProperty("SectionModificationType")
	private String sectionModificationType;
	
	@JsonProperty("IsCoverEndorsementYN")
	private String isCoverEndorsementYN;
	
	@JsonProperty("EndtShortCode")
	private String endtShortCode;
}
