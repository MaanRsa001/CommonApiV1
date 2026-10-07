package com.maan.eway.notification.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SmsTemplateRes {

	@JsonProperty("SmsSubject")
	private String smsSubject;

	@JsonProperty("SmsBody")
	private String smsBody;

	@JsonProperty("SmsRegards")
	private String smsRegards;
	
	@JsonProperty("NotificationNo")
	private String notificationNo;
	
	@JsonProperty("NotifTemplateCode")
	private String notifTemplateCode ;
	
}
