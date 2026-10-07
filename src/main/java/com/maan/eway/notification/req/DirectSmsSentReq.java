package com.maan.eway.notification.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DirectSmsSentReq {

	@JsonProperty("RequestReferenceNo")
	private String  requestReferenceNo;
	
	@JsonProperty("InsuranceId")
	private String  insuranceId ;
	
	@JsonProperty("ProductId")
	private String  productId ;

	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("SmsSubject")
	private String smsSubject;

	@JsonProperty("SmsBody")
	private String SmsBody;

	@JsonProperty("SmsRegards")
	private String SmsRegards;
	
	@JsonProperty("NotificationNo")
	private String notificationNo;
	
	@JsonProperty("NotifTemplateCode")
	private String notifTemplateCode ;
	
	@JsonProperty("CustomerReferenceNo")
	private String  customerReferenceNo;
	
	@JsonProperty("NotifTemplateDesc")
	private String notifTemplateDesc ;
	
	@JsonProperty("Customer")
	private Customer customer;
}
