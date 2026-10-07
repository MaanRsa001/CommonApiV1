package com.maan.eway.res.calc;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminReferral {

		@JsonProperty("InsuranceId")
		private String insuranceId;
		
		@JsonProperty("LoginId")
		private String loginId;
		
		@JsonProperty("UserName")
		private String userName;
		
		@JsonProperty("MailId")
		private String mailId;
		
		@JsonProperty("MobileNo")
		private String mobileNo;
		
		@JsonProperty("MobileCode")
		private String mobileCode;
		
		@JsonProperty("WhatsappCode")
		private String whatsappcode;
		
		@JsonProperty("WhatsAppNo")
		private String whatsAppNo;
		@JsonProperty("UwuserType")
	    private String     uwuserType;
		@JsonProperty("UwsubuserType")
	    private String     uwsubuserType;
		
}
