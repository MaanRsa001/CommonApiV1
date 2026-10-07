package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EndorsementChangeStatusReq {

	@JsonProperty("EndtTypeId")
	private String endtTypeId;	
	
	@JsonProperty("EndtTypeCategoryId")
	private String endtTypeCategoryId;
	
	@JsonProperty("Status")
	private String status;
		
	@JsonProperty("ProductId")
	private String productId;
		
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonProperty("CreatedBy")
	private String createdBy;

}
