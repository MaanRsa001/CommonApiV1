package com.maan.eway.notification.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DirectMailSmsSentReq {

	@JsonProperty("RequestReferenceNo")
	private String  requestReferenceNo;
	
	@JsonProperty("InsuranceId")
	private String  insuranceId ;
	
	@JsonProperty("ProductId")
	private String  productId ;
	
	@JsonProperty("NotifApplicable")
	private String notifApplicable ;
	
	@JsonProperty("NotifTemplateCode")
	private String notifTemplateCode ;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("MailSubject")
	private String mailSubject;

	@JsonProperty("MailBody")
	private String mailBody;

	@JsonProperty("MailRegards")
	private String mailRegards;
}
