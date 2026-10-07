package com.maan.eway.admin.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class GetMenuTypeReq {
	

	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("UserType")
	private List<String> Usertype;
	
	
	
}
