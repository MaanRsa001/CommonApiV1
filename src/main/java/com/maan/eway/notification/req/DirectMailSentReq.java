package com.maan.eway.notification.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DirectMailSentReq {

	@JsonProperty("RequestReferenceNo")
	private String  requestReferenceNo;
	
	@JsonProperty("InsuranceId")
	private String  insuranceId ;
	
	@JsonProperty("ProductId")
	private String  productId ;

	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("MailSubject")
	private String MailSubject;

	@JsonProperty("MailBody")
	private String mailBody;

	@JsonProperty("MailRegards")
	private String mailRegards;
	
	@JsonProperty("NotificationNo")
	private String notificationNo;
	
	@JsonProperty("NotifTemplateCode")
	private String notifTemplateCode ;
	
	@JsonProperty("AttachmentFile")
	private List<String> fileAttachment;
	
	@JsonProperty("Customer")
	private Customer customer;
	
	@JsonProperty("CustomerReferenceNo")
	private String  customerReferenceNo;
	
	@JsonProperty("NotifTemplateDesc")
	private String notifTemplateDesc ;
	
	
}
