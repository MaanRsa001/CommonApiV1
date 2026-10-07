package com.maan.eway.notification.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class NotifGetReq {

	@JsonProperty("InsuranceId")
	private String  insuranceId ;
	
	@JsonProperty("ProductId")
	private String  productId ;

	@JsonProperty("CreatedBy")
	private String createdBy;	
	
	@JsonProperty("Limit")
	private String limit;
	
	@JsonProperty("Offset")
	private String Offset;
}
