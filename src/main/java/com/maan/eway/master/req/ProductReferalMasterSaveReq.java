package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductReferalMasterSaveReq {


	@JsonProperty("ReferalId")
	private String referalId;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;

	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("ReferalName")
	private String referalName;

	@JsonProperty("ReferalDesc")
	private String referalDesc;

	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("CoreAppCode")
	private String coreAppCode;

	@JsonProperty("Remarks")
	private String remarks;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("CreatedBy")
	private String createdBy;

	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;


}
