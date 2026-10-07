package com.maan.eway.admin.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AttachEndtIdsReq {

	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("InsuranceId")
	private String insuranceId ;
	
	
	@JsonProperty("ProductId")
	private String productId ;
	
	@JsonProperty("Ids")
	private List<String> ids ;
	
	@JsonProperty("IdType")
	private String idType;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
}
