package com.maan.eway.common.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PortFolioGridReq {

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
	   
	   @JsonProperty("Limit")
	   private String     limit;
	   
	   @JsonProperty("Offset")
	   private String     offset;
	   
	   @JsonProperty("ShowAllDataYn")
	   private String     showAllDataYn;
	   
	   @JsonProperty("UserType")
	   private String  userType;
	   
	   @JsonProperty("PortFolioYn")
	   private String  portFolioYn;
}
