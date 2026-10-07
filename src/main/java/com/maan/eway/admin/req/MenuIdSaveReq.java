package com.maan.eway.admin.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MenuIdSaveReq {

	// Personal Details
	@JsonProperty("LoginId")
    private String    loginId     ;
	@JsonProperty("MenuIds")
    private List<String> menuIds   ;
	}
