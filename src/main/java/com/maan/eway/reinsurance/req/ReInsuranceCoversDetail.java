package com.maan.eway.reinsurance.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReInsuranceCoversDetail {
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("SectionName")
	private String sectionName;

	@JsonProperty("CoverId")
	private String coverId;
	
	@JsonProperty("CoverName")
	private String coverName;
	
	@JsonProperty("CoverCode")
	private String coverCode;

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("InceptionDate")
	private Date inceptionDate;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("ExpiryDate")
	private Date expiryDate;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EndStartDate")
	private Date endStartDate;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EndEndDate")
	private Date endEndDate;
	
	@JsonProperty("WarYN")
	private String warYn;
	
	@JsonProperty("SumInsuredFc")
	private String sumInsuredFc;
	
	@JsonProperty("SumInsuredLc")
	private String sumInsuredLc;
	
	@JsonProperty("PremiumFc")
	private String premiumFc;
	
	@JsonProperty("PremiumLc")
	private String premiumLc;
	
	@JsonProperty("DAFPercentage")
	private String dafPercentage;
	
	@JsonProperty("FACPercentage")
	private String facPercentage;
	
	@JsonProperty("PMLSumInsuredLc")
	private String pmlSumInsuredLc;
	
	@JsonProperty("PMLSumInsuredFc")
	private String pmlSumInsuredFc;
	
	@JsonProperty("FACSumInsuredLc")
	private String facSumInsuredLc;
	
	@JsonProperty("FACSumInsuredFc")
	private String facSumInsuredFc;
	
	@JsonProperty("FacPmlSumInsuredLc")
	private String facPmlSumInsuredLc;
	
	@JsonProperty("FacPmlSumInsuredFc")
	private String facPmlSumInsuredFc;
	
	@JsonProperty("FACPremiumLc")
	private String facPremiumLc;
	
	@JsonProperty("FACPremiumFc")
	private String facPremiumFc;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("PremiaStatus")
	private String premiaStatus;
	
	@JsonProperty("PremiaResponse")
	private String premiaResponse;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("UpdateDate")
	private Date updateDate;
	
	@JsonProperty("UpdatedBy")
	private String updatedBy;
	
	@JsonProperty("SectionCode")
	private String sectionCode;
	
	@JsonProperty("CoverRecType")
	private String coverRecType;
	
}
