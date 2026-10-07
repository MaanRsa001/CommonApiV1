package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CustomerOtherDetailsSaveReq {
	
	@JsonProperty("Mobileno2")
    private String     mobileNo2    ;
	@JsonProperty("Mobileno3")
    private String     mobileNo3    ;
	@JsonProperty("Email2")
    private String     email2       ;
	@JsonProperty("Email3")
    private String     email3       ;
	@JsonProperty("PreferredSystemNotificationId")
    private String    preferredSystemNotificationId ;
	

}
