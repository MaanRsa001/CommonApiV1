package com.maan.eway.admin.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserTypeReq {

	  @JsonProperty("UserType")
		private String userType;	
}
