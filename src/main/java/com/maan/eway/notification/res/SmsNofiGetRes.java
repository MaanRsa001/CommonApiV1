package com.maan.eway.notification.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SmsNofiGetRes {


	@JsonProperty("Sno")
	private String sno;
	
	@JsonProperty("SmsType")
	private String smsType;
	
	@JsonProperty("SmsContent")
	private String smsContent;
	
	@JsonProperty("SmsRegards")
	private String smsRegards;
	
	@JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss")
	@JsonProperty("ReqTime")
	private Date reqTime;
	
	@JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss")
	@JsonProperty("ResTime")
	private Date resTime;
	
	@JsonProperty("ResStatus")
	private String resStatus;
	
	@JsonProperty("ResMessage")
	private String resMessage;
	    

	@JsonProperty("CustomerName")
	private String customerName;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date EntryDate;
	
	@JsonProperty("MobileNo")
	private String mobileNo;
	
	@JsonProperty("SmsFrom")
	private String smsFrom;

	
	@JsonProperty("NotificationNo")
	private String notificationNo;
	
	@JsonProperty("PushedBy")
	private String pushedBy;

	
}
