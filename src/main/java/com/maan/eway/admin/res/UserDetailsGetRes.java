package com.maan.eway.admin.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserDetailsGetRes {

	@JsonProperty("LoginInformation")
    private UserLoginGetRes loginInformation      ;
	
	@JsonProperty("PersonalInformation")
    private  UserPersonalInfoGetRes personalInformation     ;
}
