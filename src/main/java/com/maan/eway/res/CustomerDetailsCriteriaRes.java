package com.maan.eway.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetailsCriteriaRes {

	@JsonProperty("CustomerId")
	private Integer customerId ;
	
	@JsonProperty("TitleDesc")
	private String titleDesc ;
	
	@JsonProperty("ClientName")
	private String clientName ;
	
	@JsonProperty("EntryDate")
	private Date entryDate ;
	
	@JsonProperty("InsuranceId")
	private String companyId ;
	
	@JsonProperty("CompanyName")
	private String companyName ;
	
	@JsonProperty("BranchCode")
	private String branchCode ;
	
	@JsonProperty("BranchName")
	private String branchName ;
	
	@JsonProperty("CreatedBy")
	private String createdBy ;
	
	@JsonProperty("UserType")
	private String userType ;
	
	@JsonProperty("SubUserType")
	private String subUserType ;
	
	@JsonProperty("ProductCount")
	private Long productCount;
	
	
	
}
