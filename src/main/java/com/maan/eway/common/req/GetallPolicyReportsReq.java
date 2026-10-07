package com.maan.eway.common.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetallPolicyReportsReq {
	
	   @JsonProperty("BranchCode")
	   private String     branchCode ;
	   @JsonProperty("InsuranceId")
	   private String     insuranceId ;
	   @JsonProperty("LoginId")
	   private String     loginId;
	   @JsonProperty("ProductId")
	   private String     productId ;
	   
	   @JsonFormat(pattern = "dd/MM/yyyy")
	   @JsonProperty("StartDate")
	   private Date     startDate;
	   
	   @JsonFormat(pattern = "dd/MM/yyyy")
	   @JsonProperty("EndDate")
	   private Date     endDate;
}
