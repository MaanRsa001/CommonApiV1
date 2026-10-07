package com.maan.eway.notification.res;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class NofiByQuoteNoRes {


	@JsonProperty("Mailyn")
	private String mailyn;
	
	@JsonProperty("Smsyn")
	private String smsyn;
	
	@JsonProperty("NotificationNo")
	private String notifNo;
	
	@JsonProperty("CustomerName")
	private String customerName;
	
	@JsonProperty("CustomerMailId")
	private String customerMailId;
	
	@JsonProperty("CustomerPhoneNo")
	private String customerPhoneNo;    

	@JsonProperty("CustomerPhoneCode")
	private String customerPhoneCode;
	
	@JsonProperty("CustomerMessengerCode")
	private String customerMessengerCode;
	
	@JsonProperty("CustomerMessengerPhone") 
	private String customerMessengerPhone;
	
	@JsonProperty("BrokerName")
	private String brokerName;
	
	@JsonProperty("BrokerCompanyName") 
	private String brokerCompanyName;
	
	@JsonProperty("BrokerMailId") 
	private String brokerMailId;
	
	@JsonProperty("BrokerPhoneNo") 
	private String brokerPhoneNo;
	
	@JsonProperty("BrokerPhoneCode")
	private String brokerPhoneCode;
	
	@JsonProperty("BrokerMessengerCode") 
	private String brokerMessengerCode;
	
	@JsonProperty("BrokerMessengerPhone")
	private String brokerMessengerPhone;
	
	@JsonProperty("UWname") 
	private String uwName;
	
	@JsonProperty("Uwmailid") 
	private String uwMailid;
	
	@JsonProperty("UWPhoneCode") 
	private String uwPhonecode;
	
	@JsonProperty("UWPhoneNo")
	private String uwPhoneNo;
	
	@JsonProperty("UWmessengercode")
	private String uwMessengerCode;
	
	@JsonProperty("UWmessengerphone")
	private String uwMessengerPhone;
	
	@JsonProperty("CompanyName") 
	private String companyName;
	
	@JsonProperty("ProductName")
	private String productName;
	
	@JsonProperty("SectionName") 
	private String sectionName;
	
	@JsonProperty("Statusmessage")
	private String statusmessage;
	
	@JsonProperty("OTP") 
	private String otp;
	
	@JsonProperty("PolicyNo") 
	private String policyNo;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("NotifDescription")
	private String notifDescription;
	
	@JsonProperty("Notiftemplatename") 
	private String notiftemplatename;
	
//	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private String EntryDate;
	
//	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("NotifcationPushdate") 
	private String notifcationPushdate;
	
	@JsonProperty("NotifpushedStatus")
	private String notifpushedStatus;
	
	@JsonProperty("Notifpusheddesc")
	private String notifpusheddesc;
	
	@JsonProperty("NotifPriority") 
	private String notifPriority;
	
	@JsonProperty("TinyURL")
	private String tinyURL;
	
	@JsonProperty("Companyid")
	private String companyid;
	
	@JsonProperty("Productid")
	private String productid;
	
//	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("NotifcationEnddate") 
	private String notifcationEnddate;
	
	@JsonProperty("CompanyAddress") 
	private String companyAddress;
	
	@JsonProperty("CompanyLogo") 
	private String companyLogo;
	
	@JsonProperty("Attachfilepath") 
	private String attachfilepath;
	
	@JsonProperty("NotiPushedBy") 
	private String notiPushedBy;
	
	//Mail
	@JsonProperty("MailSubject")
	private String mailSubject;
	
	@JsonProperty("MailBody")
	private String mailBody ;
	
	@JsonProperty("MailRegards") 
	private String mailRegards;
	
//	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PushedEntryDate") 
	private String pushedEntryDate;
	
	@JsonProperty("ToEmail")
	private String toEmail;
	
	@JsonProperty("FromeMail") 
	private String fromeMail;
	
	@JsonProperty("MailTranId") 
	private String mailTranId;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("MailResponse")
	private String mailResponse;
	
	@JsonProperty("NotifNoInMail")
	private String notifNoInMail;
	
	@JsonProperty("MailPushedBy")
	private String mailPushedby;
	
	//Sms
	@JsonProperty("Sno") 
	private String sNo;
	
	@JsonProperty("SmsFrom")
	private String smsFrom;
	
	@JsonProperty("MobileNo") 
	private String mobileNo;
	
	@JsonProperty("SmsType")
	private String smsType;
	
	@JsonProperty("SmsContent") 
	private String smsContent;
	
	//@JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss")
	@JsonProperty("ReqTime")
	private String reqTime;
	
	//@JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss")
	@JsonProperty("ResTime")
	private String resTime;
	
	
	@JsonProperty("ResStatus")
	private String resStatus;
	
	@JsonProperty("ResMessage")
	private String resMessage;
	
	@JsonProperty("SmsEntryDate")
	private String smsEntryDate;
	
	@JsonProperty("NotifNoInSms")
	private String notifNoInSms;
	@JsonProperty("SmsPushedBy") 
	private String smsPushedBy;
	
	@JsonProperty("SmsRegards")
	private String smsRegards;
	
}
