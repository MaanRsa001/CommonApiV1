package com.maan.eway.common.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class DashBoardGetRes {
	
	@JsonProperty("Email")
	private String email;

	@JsonProperty("StatusMessage")	
	private String statusmessage;
	
     @JsonFormat(pattern = "dd/MM/yyyy")
	   @JsonProperty("StartDate")
	   private Date     startDate     ;
	   @JsonFormat(pattern = "dd/MM/yyyy")
	   @JsonProperty("EndDate")
	   private Date     endDate ;
	   
	   @JsonProperty("MobileCode")	
		private String mobileCode;
	   
	   @JsonProperty("PhoneNo")	
		private String phoneno;
	   
	  
	   
}
