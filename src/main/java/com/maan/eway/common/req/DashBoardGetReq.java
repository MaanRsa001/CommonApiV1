package com.maan.eway.common.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DashBoardGetReq {



	   @JsonProperty("InsuranceId")
	   private String     insuranceId     ;
	   @JsonProperty("BranchCode")
	   private String     branchCode     ;
	   @JsonProperty("ProductId")
	   private String     productId     ;
	   @JsonProperty("LoginId")
	   private String     loginId     ;
	   @JsonProperty("UserType")
	   private String     userType     ;
	   @JsonProperty("SubUserType")
	   private String     subUserType     ;
	   @JsonFormat(pattern = "dd/MM/yyyy")
	   @JsonProperty("StartDate")
	   private Date     startDate     ;
	   @JsonFormat(pattern = "dd/MM/yyyy")
	   @JsonProperty("EndDate")
	   private Date     endDate ;
}
