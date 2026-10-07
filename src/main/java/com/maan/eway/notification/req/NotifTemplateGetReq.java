package com.maan.eway.notification.req;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class NotifTemplateGetReq {

	@JsonProperty("RequestReferenceNo")
	private String  requestReferenceNo;
	
	@JsonProperty("InsuranceId")
	private String  insuranceId ;
	
	@JsonProperty("ProductId")
	private String  productId ;
	
	@JsonProperty("NotifTemplateCode")
	private String notifTemplateCode ;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("AdditionalInfo")
	private Map<String,Object> additionalInfo;
	
}
