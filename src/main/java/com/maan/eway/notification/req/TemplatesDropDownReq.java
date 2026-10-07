package com.maan.eway.notification.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TemplatesDropDownReq {

	@JsonProperty("RequestReferenceNo")
	private String  requestReferenceNo;
	
	@JsonProperty("InsuranceId")
	private String  insuranceId ;
	@JsonProperty("ProductId")
	private String  productId ;
	
	@JsonProperty("NotifApplicable")
	private String notifApplicable ;
	
}
