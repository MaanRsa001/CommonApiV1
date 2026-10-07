package com.maan.eway.report.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GenerateAuthToken {
	
	@JsonProperty("username")
	private String username;
	
	@JsonProperty("password")
	private String password;
	

}
