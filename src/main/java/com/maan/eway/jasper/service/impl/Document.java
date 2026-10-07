package com.maan.eway.jasper.service.impl;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document {
	  
	@JsonProperty("ProductId")
	private String productId;
	@JsonProperty("agencyCode")
	private String agencycode;
	@JsonProperty("amendId")
	private String amendId;
	@JsonProperty("apiCheck")
	private String apiCheck;
	@JsonProperty("apiCheckName")
	private String apiCheckName;
	@JsonProperty("companyId")
	private String companyId;
	@JsonProperty("coreAppcode")
	private String coreAppcode;
	@JsonProperty("displayOrder")
	private String displayOrder;
	@JsonProperty("docApplicable")
	private String docApplicable;
	@JsonProperty("documentDesc")
	private String documentDesc;
	@JsonProperty("documentId")
	private String documentId;
	@JsonProperty("effectiveDate")
	private String effectiveDate;
	@JsonProperty("mandatoryStatus")
	private String mandatoryStatus;
	@JsonProperty("policyType")
	private String policyType;
	@JsonProperty("remarks")
	private String remarks;
	@JsonProperty("status")
	private String status;
	@JsonProperty("imgUrl")
	private String imgUrl;
	//private File imgUrl;
	
	
	
}
