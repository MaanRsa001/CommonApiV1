package com.maan.eway.admin.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class IssuerCraeationReq {


	@JsonProperty("LoginInformation")
    private IssuerLoginReq loginInformation      ;
	
	@JsonProperty("PersonalInformation")
    private  IssuerPersonalInfoReq personalInformation     ;
}
