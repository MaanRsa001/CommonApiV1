package com.maan.eway.admin.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class InsertUserLoginReq {

	
	@JsonProperty("LoginId")
	private String loginId ;

	@JsonProperty("MenuId")
	private List<String> menuId;
}
