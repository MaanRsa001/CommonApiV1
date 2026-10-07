package com.maan.eway.notification.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MailNotifGetRes {

	@JsonProperty("MailTranId")
	private String mailTranId;
	
	@JsonProperty("MailSubject")
	private String mailSubject;
	
	@JsonProperty("MailBody")
	private String mailBody;
	
	@JsonProperty("MailRegards")
	private String mailRegards;
		
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PushedEntryDate")
	private Date pushedEntryDate;
	
	@JsonProperty("ToMail")
	private String toMail;
	
	@JsonProperty("FromMail")
	private String fromMail;
	

	@JsonProperty("CustomerName")
	private String customerName;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("MailResponse")
	private String mailResponse;
	
	@JsonProperty("NotificationNo")
	private String notificationNo;
	
	@JsonProperty("PushedBy")
	private String pushedBy;
	

}
