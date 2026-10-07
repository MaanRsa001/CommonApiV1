package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UpdateUserInfoLoginReq {
	@JsonProperty("LoginId")
	private String loginId;

	@JsonProperty("UserMobile")
    private String userMobile ;
	
	@JsonProperty("UserMail")
	private String userMail ;
	
	@JsonProperty("MobileCode")
	  private String  mobileCode ;

}
