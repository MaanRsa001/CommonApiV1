package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ClaimIntimationGetReq {

   @JsonProperty("ClaimReferenceNo")
   @JsonFormat(shape = Shape.STRING)
   private Integer claimReferenceNo;
	 
	 @JsonProperty("ProductId")
	 private String productId;
	 
	 @JsonProperty("CompanyId")
	 private String companyId;
}
