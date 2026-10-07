package com.maan.eway.admin.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserCreationReq {

	@JsonProperty("LoginInformation")
    private UserLoginReq loginInformation      ;
	
	@JsonProperty("PersonalInformation")
    private  UserPersonalInfoReq personalInformation     ;
}
