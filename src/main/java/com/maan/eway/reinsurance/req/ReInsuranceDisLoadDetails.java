package com.maan.eway.reinsurance.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReInsuranceDisLoadDetails {
	
	@JsonProperty("CoverId")
	private String coverId;
	
	@JsonProperty("CoverCode")
	private String coverCode;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("SectionCode")
	private String sectionCode;
	
	@JsonProperty("RiskId")
	private String riskId;
	
	@JsonProperty("DiscLoadId")
	private String discLoadId;
	
	@JsonProperty("DiscLoadCode")
	private String discLoadCode;
	
	@JsonProperty("DiscLoadName")
	private String discLoadName;
	
	@JsonProperty("DiscLoadType")
	private String discLoadType;
	
	@JsonProperty("DiscLoadFc")
	private String discLoadFc;
	
	@JsonProperty("DiscLoadLc")
	private String discLoadLc;
	
	@JsonProperty("CoverRecType")
	private String coverRecType;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("CreatedDate")
	private Date createdDate;
	
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

}
