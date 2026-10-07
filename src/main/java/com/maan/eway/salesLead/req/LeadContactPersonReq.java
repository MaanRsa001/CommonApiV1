package com.maan.eway.salesLead.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LeadContactPersonReq {

	@JsonProperty("ContactType")
	private String contactType;
	
	@JsonProperty("ContactPersonName")
	private String contactPersonName;
	
	@JsonProperty("EmailAddress")
	private String emailAddress;
	
	@JsonProperty("MobileNo")
	private String mobileNo;
	
	@JsonProperty("PhoneNo")
	private String phoneNo;
	
	@JsonProperty("Designation")
	private String designation;
	
	@JsonProperty("Remarks")
	private String remarks;
	
}
