package com.maan.eway.overalldiscount;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.req.UnderwriterAdjustmentReq;

import lombok.Data;

@Data
public class FleetDetailsSaveReq {

	@JsonProperty("RequestReferenceNo")
	 private String     requestReferenceNo ;
	 
	 @JsonProperty("InsuranceId")
	 private String     insuranceId ;
	 
	 @JsonProperty("ProductId")
	 private String     productId ;
	 
	 @JsonProperty("UnderwriterAdjustments")
	private List<UnderwriterAdjustmentReq> underwriterAdjustments;
	 
	 
}
