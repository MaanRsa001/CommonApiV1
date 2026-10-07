package com.maan.eway.notification.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MailTemplateRes {


	@JsonProperty("MailSubject")
	private String mailSubject;

	@JsonProperty("MailBody")
	private String mailBody;

	@JsonProperty("MailRegards")
	private String mailRegards;
	
	@JsonProperty("NotificationNo")
	private String notificationNo;
	
	@JsonProperty("NotifTemplateCode")
	private String notifTemplateCode ;


}
