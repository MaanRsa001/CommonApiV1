package com.maan.eway.notification.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class NotificationFrameReq {

	@JsonProperty("RequestReferenceNo")
	private String  requestReferenceNo;
	
	@JsonProperty("InsuranceId")
	private String  insuranceId ;
	
	@JsonProperty("ProductId")
	private String  productId ;
	
	@JsonProperty("NotifTemplateCode")
	private String notifTemplateCode ;
	
	@JsonProperty("NotifTemplateName")
	private String notifTemplateName ;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
}
