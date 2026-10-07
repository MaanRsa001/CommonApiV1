package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductReferalChangeStatusReq {

	 @JsonProperty("InsuranceId")
	 private String companyId;
	 
	 @JsonProperty("ProductId")
	 private String productId;
	 
	 @JsonProperty("Status")
	 private String status;
	 @JsonProperty("ReferalId")
	 private String referalId;
	
	  @JsonFormat(pattern ="dd/MM/yyyy")
	  @JsonProperty("EffectiveDateStart")
	  private Date effectiveDateStart;
}
