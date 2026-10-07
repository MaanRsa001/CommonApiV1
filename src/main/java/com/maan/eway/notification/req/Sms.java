package com.maan.eway.notification.req;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Builder
@Getter
@Setter
public class Sms implements Serializable {

	/*
	 * SmsTo
		SmsSubject
		SmsBody	
		SmsRegards
	 */
	


	private String mobileNo;
	private String mobileCode;
	private String smsContent;
	private String smsRegards;
	private String smsSubject;
	private SmsConfigMasterDto master;

	private Long    notifNo ;

	/*
	private String smsToCode;	
	private String smsTo;
	private String smsSubject;
	private String smsBody;
	private String smsRegards;
	private String smsFrom;
	private String whatsappRegards;
	private JobCredentials credential;
	private String senderId;
	
*/
}
