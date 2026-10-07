package com.maan.eway.master.res;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EndorsementMasterListRes {

	@JsonProperty("EndtTypeId")
	private String endtTypeId;
	
	@JsonProperty("EndtType")
	private String endtType;
	
	@JsonProperty("EndtTypeDesc")
	private String endtTypeDesc;
		
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("Priority")
	private String priority;
	
	@JsonProperty("EndtDependantFields")
	private List<String> endtDependantFields;

	@JsonProperty("EndtDependantIds")
	private List<String> endtDependantIds;
	
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

	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date UpdatedDate;

	@JsonProperty("AmendId")
	private String amendId;


	@JsonProperty("CalcTypeId")
	private String calcTypeId;

	/*
	@JsonProperty("CalcType")
	private String calcType;
*/
	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	

	@JsonProperty("SectionModificationYn")
	private String sectionModificationYn;
	
	@JsonProperty("SectionModificationType")
	private String sectionModificationType;
	@JsonProperty("SelectedYn")
	private String selectedYn;

	@JsonProperty("IsCoverEndorsementYN")
	private String isCoverEndorsementYN;
	

	@JsonProperty("EndtShortCode")
	private String endtShortCode;
	

	@JsonProperty("EndtShortDesc")
	private String endtShortDesc;
}
