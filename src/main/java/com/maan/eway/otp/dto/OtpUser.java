package com.maan.eway.otp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class OtpUser {

    @JsonProperty("UserMailId")
	private String mailId;
    @JsonProperty("UserMobileNo")
	private String mobileNo;
    @JsonProperty("UserMobileCode")
	private String mobileCode;	    
    @JsonProperty("UserWhatsappNo")
	private String whatsappNo;
    @JsonProperty("UserWhatsappCode")
	private String whatsappCode;
    @JsonProperty("CustomerName")
	private String customerName;

}
