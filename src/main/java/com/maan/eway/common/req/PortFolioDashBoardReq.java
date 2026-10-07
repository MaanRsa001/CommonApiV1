package com.maan.eway.common.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PortFolioDashBoardReq {

	   @JsonProperty("BranchCode")
	   private String     branchCode ;
	   
	   @JsonProperty("InsuranceId")
	   private String     insuranceId ;
	   
	   @JsonProperty("ProductId")
	   private String     productId ;
	   
	   @JsonProperty("LoginId")
	   private String     loginId ;
	   
	   @JsonProperty("BusinessType")
	   private String     businessType;
	   
	   @JsonFormat(pattern = "dd/MM/yyyy")
	   @JsonProperty("StartDate")
	   private Date     startDate;
	   
	   @JsonFormat(pattern = "dd/MM/yyyy")
	   @JsonProperty("EndDate")
	   private Date     endDate;
}
