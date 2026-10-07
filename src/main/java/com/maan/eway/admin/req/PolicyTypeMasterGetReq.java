package com.maan.eway.admin.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PolicyTypeMasterGetReq implements Serializable {

    private static final long serialVersionUID = 1L;
	
		@JsonProperty("InsuranceId")
		private String insuranceId;
		
		@JsonProperty("ProductId")
		private String productId;
		
		@JsonProperty("LoginId")
		private String loginId;
}
