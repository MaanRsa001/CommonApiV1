package com.maan.eway.master.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductBenefitGetRes {


	
	@JsonProperty("BenefitId")
	private String benefitId;
	
	@JsonProperty("BenefitDescription")
	private String benefitDescription;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("CompanyName")
	private String companyName;
	
	@JsonProperty("BranchCode")
	private String branchCode;

	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("ProductDesc")
	private String productDesc;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("SectionDesc")
	private String sectionDesc;
	
	@JsonProperty("CoverId")
	private String coverId;
	
	@JsonProperty("CoverName")
	private String coverName;
	
	@JsonProperty("SubCoverId")
	private String subCoverId;
	
	@JsonProperty("SubCoverName")
	private String subCoverName;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("LongDesc")
	private String longDesc;

	@JsonProperty("UpdatedBy")
	private String updatedBy;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date updatedDate;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("CalcType")
	private String calcType;
	
	@JsonProperty("Value")
	private String value;
	
	@JsonProperty("AgencyCode")
	private String agencyCode;

	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;

	@JsonProperty("TypeId")
	private String typeId;

	@JsonProperty("TypeDesc")
	private String typeDesc;

}
