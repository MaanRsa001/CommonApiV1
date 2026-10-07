package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetApproverListRes {
	   
	   @JsonProperty("LoginId")
	   private String   loginId ;
}
